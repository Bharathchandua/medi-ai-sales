package com.medi.ai.sales.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.otpService = otpService;
    }

    public void sendRegistrationOtp(AuthRequest.SendOtpRequest request) {
        // Validation: Verify if username or email is already taken
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        // Generate the 6-digit OTP
        String otp = otpService.generateOtp(request.email());
        // Print the OTP in a highly visible box in the Spring Boot console!
        System.out.println("\n");
        System.out.println("##################################################");
        System.out.println("              [MEDI-AI-SALES OTP]                 ");
        System.out.println("      REGISTRATION OTP FOR: " + request.email());
        System.out.println("      YOUR 6-DIGIT VERIFICATION CODE IS: " + otp);
        System.out.println("##################################################");
        System.out.println("\n");
    }

    public AuthResponse register(AuthRequest.RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        // Validate the OTP
        boolean isOtpValid = otpService.validateOtp(request.email(), request.otp());
        if (!isOtpValid) {
            throw new IllegalArgumentException("Invalid or expired OTP code");
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                request.phone(),
                request.role()
        );

        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public AuthResponse login(AuthRequest.LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    // 4. Sends recovery OTP for Forgot Password
    public void sendForgotPasswordOtp(AuthRequest.ForgotPasswordSendOtpRequest request) {
        // Verify user exists with this email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));
        // Generate OTP
        String otp = otpService.generateOtp(user.getEmail());
        // Print recovery OTP to console
        System.out.println("\n");
        System.out.println("##################################################");
        System.out.println("              [MEDI-AI-SALES OTP]                 ");
        System.out.println("      PASSWORD RESET OTP FOR: " + request.email());
        System.out.println("      YOUR 6-DIGIT RECOVERY CODE IS: " + otp);
        System.out.println("##################################################");
        System.out.println("\n");
    }
    // 5. Verifies recovery OTP and resets password
    public void resetPassword(AuthRequest.ResetPasswordRequest request) {
        // Validate OTP
        boolean isOtpValid = otpService.validateOtp(request.email(), request.otp());
        if (!isOtpValid) {
            throw new IllegalArgumentException("Invalid or expired OTP code");
        }
        // Retrieve user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));
        // Encrypt and update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public String setupDefaultAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return "Admin already exists!";
        }
        User admin = new User(
                "admin",
                passwordEncoder.encode("admin123"),
                "admin@medi.com",
                "1234567890",
                Role.ADMIN
        );
        userRepository.save(admin);
        return "Admin account successfully created! Username: admin, Password: admin123";
    }

    public String resetUser() {
        User user = userRepository.findByUsername("bharath").orElse(null);
        if (user == null) {
            return "User bharath not found!";
        }
        user.setPassword(passwordEncoder.encode("Password123"));
        userRepository.save(user);
        return "Password for user 'bharath' has been reset to: Password123";
    }

    public String testDbRegister() {
        try {
            userRepository.findByUsername("testuser").ifPresent(userRepository::delete);
            userRepository.findByEmail("test@test.com").ifPresent(userRepository::delete);

            User testUser = new User(
                    "testuser",
                    passwordEncoder.encode("password123"),
                    "test@test.com",
                    "1234567890",
                    Role.ADMIN
            );
            userRepository.save(testUser);
            return "SUCCESS: Test user registered successfully!";
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            return "ERROR: " + e.getMessage() + "\n\nSTACKTRACE:\n" + sw.toString();
        }
    }
}
