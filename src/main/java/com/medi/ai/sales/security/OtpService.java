package com.medi.ai.sales.security;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // 1. Inject the Java Mail Sender bean
    private final JavaMailSender mailSender;
    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static class OtpData {
        String code;
        LocalDateTime expiryTime;

        OtpData(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
    }

    // 2. Generate and send the OTP
    public String generateOtp(String key) {
        String otp = String.format("%06d", random.nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5); // Expires in 5 minutes

        otpCache.put(key, new OtpData(otp, expiry));

        // Call our email transmitter helper
        sendOtpEmail(key, otp);

        return otp;
    }

    // 3. Email transmission method with local console fallback
    private void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Medi-AI-Sales OTP Verification");
            message.setText("Hello,\n\nYour 6-digit verification code is: " + otp +
                    "\n\nThis code is valid for 5 minutes. If you did not request this, please ignore this email.\n\nBest regards,\nMedi-AI-Sales Team");

            mailSender.send(message);
            System.out.println(">>> OTP successfully sent to email: " + email);
        } catch (Exception e) {
            // Safe Fallback: If mail properties are missing, print OTP in console
            System.out.println("\n");
            System.out.println("==================================================");
            System.out.println("[WARNING] Could not send real email OTP (SMTP settings not configured).");
            System.out.println(">>> REGISTRATION OTP FOR: " + email);
            System.out.println(">>> YOUR 6-DIGIT CODE IS: " + otp);
            System.out.println("==================================================");
            System.out.println("\n");
        }
    }

    // 4. Validate OTP
    public boolean validateOtp(String key, String enteredOtp) {
        OtpData storedData = otpCache.get(key);

        if (storedData == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(storedData.expiryTime)) {
            otpCache.remove(key);
            return false;
        }

        boolean isValid = storedData.code.equals(enteredOtp);
        if (isValid) {
            otpCache.remove(key);
        }

        return isValid;
    }
}