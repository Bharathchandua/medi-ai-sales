package com.medi.ai.sales.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record AuthRequest() {

    public record RegisterRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password,
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
            @NotBlank(message = "Phone number is required") String phone,
            @NotBlank(message = "OTP is required") @Size(min = 6, max = 6, message = "OTP must be 6 digits") String otp,
            @NotNull(message = "Role is required") Role role
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password
    ) {
    }

    // 3. DTO to request a signup verification OTP
    public record SendOtpRequest(
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
            @NotBlank(message = "Username is required") String username
    ) {
    }
    // 4. DTO to request a password recovery OTP
    public record ForgotPasswordSendOtpRequest(
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email
    ) {
    }
    // 5. DTO to complete password reset
    public record ResetPasswordRequest(
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
            @NotBlank(message = "OTP is required") String otp,
            @NotBlank(message = "New password is required") String newPassword
    ) {
    }
}
