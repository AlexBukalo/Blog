package main.controllers;

import main.request.LoginRequest;
import main.request.ProfileRequest;
import main.request.RegistrationRequest;
import main.service.CaptchaService;
import main.service.LoginService;
import main.service.RegistrationService;
import main.service.Response.DefaultResponse;
import main.service.dto.CaptchaDto;
import main.service.Response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(@AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(loginService.check(principal));
    }

    @GetMapping("/logout")
    public ResponseEntity<LoginResponse> logout(HttpServletRequest request) {
        return ResponseEntity.ok(loginService.logout(request));
    }

    @GetMapping("/captcha")
    public CaptchaDto getCaptcha() {
        return captchaService.getCaptcha();
    }

    @PostMapping("/register")
    public DefaultResponse register(@RequestBody RegistrationRequest registrationRequest) {
        return registrationService.registration(registrationRequest);
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public DefaultResponse restore(@RequestBody ProfileRequest request) {
        System.out.println(request.getEmail());
        return registrationService.restore(request.getEmail());
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public DefaultResponse restorePassword(@RequestBody RegistrationRequest registrationRequest) {
        return registrationService.restorePassword(registrationRequest);
    }

}
