package main.service;

import com.github.cage.Cage;

import com.github.cage.image.Painter;
import main.model.CaptchaCode;
import main.model.repository.CaptchaRepository;
import main.service.dto.CaptchaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Base64;


@Service
public class CaptchaService {

    @Autowired
    private CaptchaRepository captchaRepository;

    public CaptchaDto getCaptcha() {
        Painter painter = new Painter(100, 35, null, null, null, null);
        Cage cage = new Cage(painter, null, null, null, null, null, null);
        String token = cage.getTokenGenerator().next().substring(0, 4);

        byte[] captcha = cage.draw(token);
        String encodedString = Base64.getEncoder().encodeToString(captcha);

        CaptchaCode captchaCode = new CaptchaCode(new Timestamp(System.currentTimeMillis()), token, new BCryptPasswordEncoder(4));
        captchaRepository.save(captchaCode);

        CaptchaDto captchaDto = new CaptchaDto();
        captchaDto.setSecret(captchaCode.getSecretCode());
        captchaDto.setImage("data:image/png;base64, " + encodedString);

        return captchaDto;
    }

    public void deleteCaptcha() {
        captchaRepository.deleteCaptcha();
    }

    public CaptchaCode findCaptcha(String code, String secretCode) {
        return captchaRepository.getCaptcha(code, secretCode).get();
    }

    //Time in milliseconds
    @Scheduled(fixedRate = 50000000)
    public void deleteCaptchaScheduled() {
        deleteCaptcha();
    }
}
