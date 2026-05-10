package com.ledgera.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final Resend resend;

    @Value("${resend.from.email}")
    private String fromEmail;

    @Value("${resend.from.name}")
    private String fromName;

    @Value("${app.base.url}")
    private String appBaseUrl;

    public EmailService(Resend resend) {
        this.resend = resend;
    }

    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        try {
            String resetLink = appBaseUrl + "/reset-password?token=" + resetToken;
            String htmlContent = buildPasswordResetEmailHtml(userName, resetLink, resetToken);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(toEmail)
                    .subject("Reset Your Ledgera Password")
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Password reset email sent successfully to {} with ID: {}", toEmail, response.getId());

        } catch (ResendException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email. Please try again later.");
        }
    }

    private String buildPasswordResetEmailHtml(String userName, String resetLink, String resetToken) {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Your Password</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', Roboto, 'Helvetica Neue', Arial, sans-serif;
            line-height: 1.6;
            color: #1f2937;
            background-color: #f9fafb;
        }
        .email-container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
        }
        .header {
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            padding: 40px 30px;
            text-align: center;
        }
        .logo-container {
            display: inline-block;
            background-color: rgba(255, 255, 255, 0.15);
            padding: 16px;
            border-radius: 16px;
            margin-bottom: 16px;
        }
        .logo {
            width: 48px;
            height: 48px;
            display: block;
        }
        .header-title {
            color: #ffffff;
            font-size: 24px;
            font-weight: 700;
            margin: 0;
            letter-spacing: -0.5px;
        }
        .content {
            padding: 40px 30px;
        }
        .greeting {
            font-size: 18px;
            font-weight: 600;
            color: #111827;
            margin-bottom: 16px;
        }
        .message {
            font-size: 15px;
            color: #4b5563;
            margin-bottom: 24px;
            line-height: 1.7;
        }
        .button-container {
            text-align: center;
            margin: 32px 0;
        }
        .reset-button {
            display: inline-block;
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            color: #ffffff;
            text-decoration: none;
            padding: 16px 32px;
            border-radius: 12px;
            font-weight: 600;
            font-size: 15px;
            box-shadow: 0 4px 6px -1px rgba(99, 102, 241, 0.3);
            transition: all 0.2s;
        }
        .reset-button:hover {
            box-shadow: 0 10px 15px -3px rgba(99, 102, 241, 0.4);
        }
        .expiry-notice {
            background-color: #fef3c7;
            border-left: 4px solid #f59e0b;
            padding: 16px;
            margin: 24px 0;
            border-radius: 8px;
        }
        .expiry-notice p {
            font-size: 14px;
            color: #92400e;
            margin: 0;
        }
        .expiry-notice strong {
            color: #78350f;
        }
        .fallback-section {
            margin-top: 32px;
            padding-top: 24px;
            border-top: 1px solid #e5e7eb;
        }
        .fallback-title {
            font-size: 14px;
            font-weight: 600;
            color: #6b7280;
            margin-bottom: 12px;
        }
        .fallback-link {
            font-size: 13px;
            color: #6366f1;
            word-break: break-all;
            text-decoration: none;
        }
        .token-box {
            background-color: #f3f4f6;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 12px;
            margin-top: 8px;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            color: #374151;
            word-break: break-all;
        }
        .security-notice {
            margin-top: 24px;
            padding: 16px;
            background-color: #f9fafb;
            border-radius: 8px;
        }
        .security-notice p {
            font-size: 13px;
            color: #6b7280;
            margin: 0;
        }
        .footer {
            background-color: #0f0f14;
            padding: 32px 30px;
            text-align: center;
        }
        .footer-brand {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            margin-bottom: 16px;
        }
        .footer-logo {
            width: 32px;
            height: 32px;
        }
        .footer-name {
            color: #ffffff;
            font-size: 16px;
            font-weight: 600;
        }
        .footer-text {
            font-size: 13px;
            color: #9ca3af;
            margin: 8px 0;
        }
        .footer-links {
            margin-top: 16px;
        }
        .footer-link {
            color: #6366f1;
            text-decoration: none;
            font-size: 13px;
            margin: 0 12px;
        }
        .footer-link:hover {
            text-decoration: underline;
        }
        @media only screen and (max-width: 600px) {
            .content {
                padding: 30px 20px;
            }
            .header {
                padding: 30px 20px;
            }
            .footer {
                padding: 24px 20px;
            }
            .reset-button {
                padding: 14px 28px;
                font-size: 14px;
            }
        }
    </style>
</head>
<body>
    <div class="email-container">
        <!-- Header -->
        <div class="header">
            <div class="logo-container">
                <svg class="logo" width="48" height="48" viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect width="512" height="512" rx="96" fill="white" fill-opacity="0.2"/>
                    <g transform="translate(128, 128)">
                        <rect x="20" y="20" width="40" height="236" rx="8" fill="white" opacity="0.9"/>
                        <rect x="60" y="20" width="196" height="236" rx="12" fill="white"/>
                        <line x1="90" y1="70" x2="226" y2="70" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <line x1="90" y1="110" x2="226" y2="110" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <line x1="90" y1="150" x2="226" y2="150" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <line x1="90" y1="190" x2="226" y2="190" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <circle cx="158" cy="130" r="50" fill="#6366f1" opacity="0.15"/>
                        <text x="158" y="155" font-family="Arial, sans-serif" font-size="60" font-weight="bold" fill="#6366f1" text-anchor="middle">$</text>
                    </g>
                </svg>
            </div>
            <h1 class="header-title">Ledgera</h1>
        </div>

        <!-- Content -->
        <div class="content">
            <p class="greeting">Hi %s,</p>
            
            <p class="message">
                We received a request to reset your password for your Ledgera account. 
                Click the button below to create a new password.
            </p>

            <div class="button-container">
                <a href="%s" class="reset-button">Reset Password</a>
            </div>

            <div class="expiry-notice">
                <p><strong>⏱️ This link expires in 15 minutes</strong></p>
                <p>For security reasons, this password reset link will only work once and expires shortly.</p>
            </div>

            <div class="fallback-section">
                <p class="fallback-title">Button not working? Copy and paste this link:</p>
                <a href="%s" class="fallback-link">%s</a>
                
                <p class="fallback-title" style="margin-top: 16px;">Or use this reset token:</p>
                <div class="token-box">%s</div>
            </div>

            <div class="security-notice">
                <p>
                    <strong>🔒 Security Notice:</strong> If you didn't request this password reset, 
                    please ignore this email or contact support if you have concerns about your account security.
                </p>
            </div>
        </div>

        <!-- Footer -->
        <div class="footer">
            <div class="footer-brand">
                <svg class="footer-logo" width="32" height="32" viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect width="512" height="512" rx="96" fill="url(#bgGradient)"/>
                    <defs>
                        <linearGradient id="bgGradient" x1="0%%" y1="0%%" x2="100%%" y2="100%%">
                            <stop offset="0%%" style="stop-color:#6366f1;stop-opacity:1" />
                            <stop offset="100%%" style="stop-color:#8b5cf6;stop-opacity:1" />
                        </linearGradient>
                    </defs>
                    <g transform="translate(128, 128)">
                        <rect x="20" y="20" width="40" height="236" rx="8" fill="white" opacity="0.9"/>
                        <rect x="60" y="20" width="196" height="236" rx="12" fill="white"/>
                        <line x1="90" y1="70" x2="226" y2="70" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <line x1="90" y1="110" x2="226" y2="110" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <line x1="90" y1="150" x2="226" y2="150" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <line x1="90" y1="190" x2="226" y2="190" stroke="#6366f1" stroke-width="4" stroke-linecap="round"/>
                        <circle cx="158" cy="130" r="50" fill="#6366f1" opacity="0.15"/>
                        <text x="158" y="155" font-family="Arial, sans-serif" font-size="60" font-weight="bold" fill="#6366f1" text-anchor="middle">$</text>
                    </g>
                </svg>
                <span class="footer-name">Ledgera</span>
            </div>
            
            <p class="footer-text">Modern Finance Workspace</p>
            <p class="footer-text">Track expenses, manage records, and oversee your team.</p>
            
            <div class="footer-links">
                <a href="%s" class="footer-link">Visit Dashboard</a>
                <a href="%s/login" class="footer-link">Sign In</a>
            </div>
            
            <p class="footer-text" style="margin-top: 16px;">
                © %d Ledgera. All rights reserved.
            </p>
        </div>
    </div>
</body>
</html>
                """.formatted(
                userName,
                resetLink,
                resetLink,
                resetLink,
                resetToken,
                appBaseUrl,
                appBaseUrl,
                java.time.Year.now().getValue()
        );
    }
}
