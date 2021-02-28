package main.service;

import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import main.request.LoginRequest;
import main.request.ProfileRequest;
import main.service.Response.DefaultResponse;
import main.service.Response.LoginResponse;
import main.service.Response.UserLoginResponse;
import main.service.dto.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Objects;
import java.util.Random;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, UserRepository userRepository, PostRepository postRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth;
        try {
            auth = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthenticationException authenticationException) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setResult(false);
            return loginResponse;
        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();

        return getLoginResponse(user.getUsername());
    }

    public LoginResponse check(Principal principal) {
        if (principal == null) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setResult(false);
            return loginResponse;
        }
        return getLoginResponse(principal.getName());
    }

    public LoginResponse logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        return loginResponse;
    }

    public DefaultResponse redProfile(ProfileRequest profileRequest, User user) {
        main.model.User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        DefaultResponse response = new DefaultResponse();
        Errors errors = new Errors();
        System.out.println("Name" + profileRequest.getName());
        System.out.println("Email" + profileRequest.getEmail());
        System.out.println("Password" + profileRequest.getPassword());
        System.out.println("Photo" + profileRequest.getPhoto());
        System.out.println("Remove" + profileRequest.getRemovePhoto());


        userDB.setName(profileRequest.getName());
        if (!userDB.getEmail().equals(profileRequest.getEmail()) && userRepository.findByEmail(profileRequest.getEmail()).isPresent()) {
            response.setResult(false);
            errors.setEmail("Этот e-mail уже зарегистрирован");
            response.setErrors(errors);
        } else userDB.setEmail(profileRequest.getEmail());
        if (profileRequest.getPassword() != null) {
            if (profileRequest.getPassword().length() < 6) {
                response.setResult(false);
                errors.setPassword("Пароль короче 6-ти символов");
                response.setErrors(errors);
            } else userDB.setPassword(new BCryptPasswordEncoder(12), profileRequest.getPassword());
        }
        if (profileRequest.getRemovePhoto() != null) {
            userDB.setPhoto(profileRequest.getPhoto());
        }
        userRepository.save(userDB);


        return response;
    }

    public DefaultResponse redProfilePhoto(String name, String email, String password, MultipartFile photo, Integer removePhoto, User user, HttpServletRequest request) {
        main.model.User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        DefaultResponse response = new DefaultResponse();
        Errors errors = new Errors();
        System.out.println("Name" + name);
        System.out.println("Email" + email);
        System.out.println("Password" + password);
        System.out.println("Photo" + photo);
        System.out.println("Remove" + removePhoto);

        userDB.setName(name);
        if (!userDB.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            response.setResult(false);
            errors.setEmail("Этот e-mail уже зарегистрирован");
            response.setErrors(errors);
        } else userDB.setEmail(email);
        if (password != null) {
            if (password.length() < 6) {
                response.setResult(false);
                errors.setPassword("Пароль короче 6-ти символов");
                response.setErrors(errors);
            } else userDB.setPassword(new BCryptPasswordEncoder(12), password);
        }

        if (photo.getSize() > 5_242_880) {
            response.setResult(false);
            errors.setPhoto("Фото слишком большое, нужно не более 5мб");
            response.setErrors(errors);
        } else userDB.setPhoto(image(photo, request));

        userRepository.save(userDB);

        return response;
    }

    private LoginResponse getLoginResponse(String email) {
        main.model.User userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        UserLoginResponse userLoginResponse = new UserLoginResponse(userModel
                , postRepository.postsForModeration().size());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUser(userLoginResponse);
        return loginResponse;
    }

    private String image(MultipartFile photo, HttpServletRequest request) {
        String path = null;
        String file = null;
        try {
            file = Objects.requireNonNull(photo.getOriginalFilename()).toLowerCase();
            Random r = new Random();
            path = "/avatars/" + (char) (r.nextInt(26) + 'a') + (char) (r.nextInt(26) + 'a') + "/" +
                    (char) (r.nextInt(26) + 'a') + (char) (r.nextInt(26) + 'a') + "/" +
                    (char) (r.nextInt(26) + 'a') + (char) (r.nextInt(26) + 'a') + "/";
            String realPath = request.getServletContext().getRealPath(path);
            Path pathFile = Path.of(realPath);
            if (!Files.exists(pathFile)) {
                Files.createDirectories(Path.of(realPath));
            }
            File transferFile = new File(realPath + "/" + file);

            InputStream is = new ByteArrayInputStream(photo.getBytes());
            BufferedImage newBi = ImageIO.read(is);
            newBi = newBi.getSubimage(newBi.getWidth() / 2, newBi.getHeight() / 2, 36, 36);
            ByteArrayOutputStream streamByte = new ByteArrayOutputStream();
            ImageIO.write(newBi, "jpg", streamByte);

            byte[] bytes = streamByte.toByteArray();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(transferFile));
            stream.write(bytes);
            stream.close();
        } catch (IOException ignore) {
        }
        return path + file;
    }
}
