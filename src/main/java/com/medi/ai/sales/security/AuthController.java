package com.medi.ai.sales.security;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/register/send-otp")
    @ResponseStatus(HttpStatus.OK)
    public void sendRegistrationOtp(@Valid @RequestBody AuthRequest.SendOtpRequest request) {
        authService.sendRegistrationOtp(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest.LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password/send-otp")
    @ResponseStatus(HttpStatus.OK)
    public void sendForgotPasswordOtp(@Valid @RequestBody AuthRequest.ForgotPasswordSendOtpRequest request) {
        authService.sendForgotPasswordOtp(request);
    }

    @PostMapping("/forgot-password/reset")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@Valid @RequestBody AuthRequest.ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

    @org.springframework.web.bind.annotation.GetMapping("/setup-default-admin")
    public String setupDefaultAdmin() {
        return authService.setupDefaultAdmin();
    }

    @org.springframework.web.bind.annotation.GetMapping("/reset-user")
    public String resetUser() {
        return authService.resetUser();
    }

    @org.springframework.web.bind.annotation.GetMapping("/test-db-register")
    public String testDbRegister() {
        return authService.testDbRegister();
    }

    @org.springframework.web.bind.annotation.GetMapping("/list-users")
    public String listUsers() {
        return authService.listUsers();
    }
}
