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

    // 3. Email transmission method running asynchronously via Brevo HTTP API
    private void sendOtpEmail(String email, String otp) {
        new Thread(() -> {
            try {
                String apiKey = System.getenv("BREVO_API_KEY");
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    throw new IllegalStateException("BREVO_API_KEY environment variable is not configured.");
                }
                
                java.net.URL url = new java.net.URL("https://api.brevo.com/v3/smtp/email");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("accept", "application/json");
                conn.setRequestProperty("api-key", apiKey);
                conn.setRequestProperty("content-type", "application/json");
                conn.setDoOutput(true);

                String jsonPayload = "{"
                        + "\"sender\":{\"name\":\"Medi-AI-Sales\",\"email\":\"bharath.anand.m@gmail.com\"},"
                        + "\"to\":[{\"email\":\"" + email + "\"}],"
                        + "\"subject\":\"Medi-AI-Sales OTP Verification\","
                        + "\"htmlContent\":\"<html><body>"
                        + "<h2>Medi-AI-Sales Verification</h2>"
                        + "<p>Hello,</p>"
                        + "<p>Your 6-digit verification code is: <strong>" + otp + "</strong></p>"
                        + "<p>This code is valid for 5 minutes. If you did not request this code, please ignore this email.</p>"
                        + "<br/><p>Best regards,<br/>Medi-AI-Sales Team</p>"
                        + "</body></html>\""
                        + "}";

                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                if (code >= 200 && code < 300) {
                    System.out.println(">>> OTP successfully sent via Brevo HTTP API to: " + email);
                } else {
                    throw new RuntimeException("Brevo API responded with HTTP error code: " + code);
                }
            } catch (Exception e) {
                // Safe Fallback: If HTTP call fails, print OTP in console
                System.out.println("\n");
                System.out.println("==================================================");
                System.out.println("[WARNING] Could not send real email OTP via Brevo API: " + e.getMessage());
                System.out.println(">>> REGISTRATION OTP FOR: " + email);
                System.out.println(">>> YOUR 6-DIGIT CODE IS: " + otp);
                System.out.println("==================================================");
                System.out.println("\n");
            }
        }).start();
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