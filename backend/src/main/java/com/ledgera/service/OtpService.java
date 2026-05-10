package com.ledgera.service;

import com.ledgera.dto.MessageResponse;
import com.ledgera.dto.RequestOtpRequest;
import com.ledgera.dto.ResetPasswordWithOtpRequest;
import com.ledgera.dto.VerifyOtpRequest;
import com.ledgera.entity.User;
import com.ledgera.exception.BadRequestException;
import com.ledgera.repository.UserRepository;
import com.ledgera.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, Bucket> passwordResetBuckets;
    private final RateLimitConfig rateLimitConfig;
    private final SecureRandom secureRandom;

    public OtpService(UserRepository userRepository,
                      EmailService emailService,
                      PasswordEncoder passwordEncoder,
                      Map<String, Bucket> passwordResetBuckets,
                      RateLimitConfig rateLimitConfig) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetBuckets = passwordResetBuckets;
        this.rateLimitConfig = rateLimitConfig;
        this.secureRandom = new SecureRandom();
    }

    @Transactional
    public MessageResponse requestOtp(RequestOtpRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        log.info("=== OTP REQUEST START ===");
        log.info("Email: {}", email);

        // Rate limiting
        Bucket bucket = passwordResetBuckets.computeIfAbsent(email,
                k -> rateLimitConfig.createPasswordResetBucket());

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for OTP request: {}", email);
            throw new BadRequestException("Too many OTP requests. Please try again later.");
        }

        // Find user
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("No account found for email: {}", email);
            // Return success to prevent email enumeration
            return MessageResponse.builder()
                    .message("If an account exists with this email, you will receive an OTP.")
                    .build();
        }

        // Check resend cooldown
        if (user.getOtpLastSent() != null) {
            LocalDateTime cooldownEnd = user.getOtpLastSent().plusSeconds(RESEND_COOLDOWN_SECONDS);
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), cooldownEnd).getSeconds();
                throw new BadRequestException("Please wait " + secondsRemaining + " seconds before requesting a new OTP.");
            }
        }

        // Generate OTP
        String otp = generateOtp();
        log.info("OTP generated for user: {}", user.getName());

        // Save OTP
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        user.setOtpAttempts(0);
        user.setOtpLastSent(LocalDateTime.now());

        try {
            userRepository.save(user);
            log.info("OTP saved to database");
        } catch (Exception e) {
            log.error("Failed to save OTP: {}", e.getMessage(), e);
            throw new BadRequestException("Unable to process request. Please try again.");
        }

        // Send OTP email
        try {
            log.info("Sending OTP email...");
            emailService.sendOtpEmail(user.getEmail(), user.getName(), otp);
            log.info("=== OTP REQUEST SUCCESS ===");
        } catch (Exception e) {
            log.error("Failed to send OTP email: {}", e.getMessage(), e);
            // Rollback OTP
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            user.setOtpAttempts(0);
            user.setOtpLastSent(null);
            userRepository.save(user);
            throw new BadRequestException("Failed to send OTP. Please try again later.");
        }

        return MessageResponse.builder()
                .message("If an account exists with this email, you will receive an OTP.")
                .build();
    }

    @Transactional
    public MessageResponse verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        String otp = request.getOtp().trim();

        log.info("=== OTP VERIFICATION START ===");
        log.info("Email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        // Check if OTP exists
        if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
            throw new BadRequestException("No OTP found. Please request a new one.");
        }

        // Check expiry
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            user.setOtpAttempts(0);
            userRepository.save(user);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Check attempts
        if (user.getOtpAttempts() >= MAX_OTP_ATTEMPTS) {
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            user.setOtpAttempts(0);
            userRepository.save(user);
            throw new BadRequestException("Too many failed attempts. Please request a new OTP.");
        }

        // Verify OTP
        if (!otp.equals(user.getOtpCode())) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            userRepository.save(user);
            int attemptsLeft = MAX_OTP_ATTEMPTS - user.getOtpAttempts();
            throw new BadRequestException("Invalid OTP. " + attemptsLeft + " attempts remaining.");
        }

        log.info("=== OTP VERIFICATION SUCCESS ===");

        return MessageResponse.builder()
                .message("OTP verified successfully")
                .build();
    }

    @Transactional
    public MessageResponse resetPasswordWithOtp(ResetPasswordWithOtpRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        String otp = request.getOtp().trim();

        log.info("=== PASSWORD RESET WITH OTP START ===");
        log.info("Email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        // Check if OTP exists
        if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
            throw new BadRequestException("No OTP found. Please request a new one.");
        }

        // Check expiry
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            user.setOtpAttempts(0);
            userRepository.save(user);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Check attempts
        if (user.getOtpAttempts() >= MAX_OTP_ATTEMPTS) {
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            user.setOtpAttempts(0);
            userRepository.save(user);
            throw new BadRequestException("Too many failed attempts. Please request a new OTP.");
        }

        // Verify OTP
        if (!otp.equals(user.getOtpCode())) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            userRepository.save(user);
            int attemptsLeft = MAX_OTP_ATTEMPTS - user.getOtpAttempts();
            throw new BadRequestException("Invalid OTP. " + attemptsLeft + " attempts remaining.");
        }

        // Reset password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        // Clear OTP
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setOtpAttempts(0);
        user.setOtpLastSent(null);
        
        userRepository.save(user);

        log.info("=== PASSWORD RESET WITH OTP SUCCESS ===");

        return MessageResponse.builder()
                .message("Password has been reset successfully")
                .build();
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(900000) + 100000; // 6-digit number
        return String.valueOf(otp);
    }
}
