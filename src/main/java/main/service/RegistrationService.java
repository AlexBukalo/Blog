package main.service;

import main.model.CaptchaCode;
import main.model.User;
import main.model.repository.UserRepository;
import main.request.RegistrationRequest;
import main.service.Response.DefaultResponse;
import main.service.dto.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaptchaService captchaService;

    public DefaultResponse registration(RegistrationRequest request) {
        captchaService.deleteCaptcha();
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        Errors errors = new Errors();
        DefaultResponse response = new DefaultResponse();
        CaptchaCode captchaCode = null;

        try {
            captchaCode = captchaService.findCaptcha(request.getCaptcha(), request.getCaptchaSecret());
        } catch (NoSuchElementException ignored) {
        }

        if (user.isEmpty() && !request.getName().equals("") && request.getPassword().length() >= 6
                && captchaCode != null) {
            response.setResult(true);
            User newUser = new User();
            newUser.setEmail(request.getEmail());
            newUser.setRegTime(new Timestamp(System.currentTimeMillis()));
            newUser.setPassword(new BCryptPasswordEncoder(12), request.getPassword());
            newUser.setName(request.getName());
            short isModerator = 0;
            newUser.setIsModerator(isModerator);
            userRepository.save(newUser);
        } else {
            response.setResult(false);
            if (user.isPresent()) {
                errors.setEmail("Этот e-mail уже зарегистрирован");
            }
            if (request.getName().equals("")) {
                errors.setName("Имя указано неверно");
            }
            if (request.getPassword().length() < 6) {
                errors.setPassword("Пароль короче 6-ти символов");
            }
            if (captchaCode == null) {
                errors.setCaptcha("Код с картинки введён неверно");
            }
            response.setErrors(errors);
        }

        return response;
    }

    public DefaultResponse restore(String email) {
        DefaultResponse response = new DefaultResponse();
        if(userRepository.findByEmail(email).isPresent()) {
            MailSender mailSender = new MailSender("sobaka834@gmail.com", "Polina123");
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("not found"));
            String code = new BigInteger(200, new Random()).toString(32);
            user.setCode(code);
            userRepository.save(user);
            response.setResult(true);
            mailSender.send("Восстановление пароля", "Ссылка для восстановаления пароля: https://blogalexbukalo.herokuapp.com/" + code, "sobaka834@gmail.com", email);
        } else response.setResult(false);

        return response;
    }

    public DefaultResponse restorePassword(RegistrationRequest registrationRequest) {
        captchaService.deleteCaptcha();
        Optional<User> userDB = userRepository.findByCode(registrationRequest.getCode());
        Errors errors = new Errors();
        DefaultResponse response = new DefaultResponse();
        CaptchaCode captchaCode = null;

        try {
            captchaCode = captchaService.findCaptcha(registrationRequest.getCaptcha(), registrationRequest.getCaptchaSecret());
        } catch (NoSuchElementException ignored) {
        }

        if(userDB.isPresent() && captchaCode != null && registrationRequest.getPassword().length() >= 6) {
            User user = userDB.get();
            user.setPassword(new BCryptPasswordEncoder(12), registrationRequest.getPassword());
            userRepository.save(user);
            response.setResult(true);
            System.out.println("Все прошло ок");
        } else {
            if(userDB.isEmpty()) {
                errors.setCode(" \"Ссылка для восстановления пароля устарела.\n" +
                        "<a href=\n" +
                        "\\\"/auth/restore\\\">Запросить ссылку снова</");
            }
            if(captchaCode == null) {
                response.setResult(false);
                errors.setCaptcha("Код с картинки введён неверно");
            }
            if (registrationRequest.getPassword().length() < 6) {
                errors.setPassword("Пароль короче 6-ти символов");
            }
            response.setErrors(errors);
        }
        return response;
    }


}
