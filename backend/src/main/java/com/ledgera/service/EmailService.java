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
        log.info("=== EMAIL SEND START ===");
        log.info("To: {}", toEmail);
        log.info("From Name: {}", fromName);
        log.info("From Email: {}", fromEmail);
        log.info("App Base URL: {}", appBaseUrl);
        log.info("Resend client: {}", resend != null ? "initialized" : "NULL!");

        String fromAddress = fromName + " <" + fromEmail + ">";
        log.info("Full from address: {}", fromAddress);

        String resetLink = appBaseUrl + "/reset-password?token=" + resetToken;
        log.info("Reset link: {}", resetLink);

        String htmlContent = buildPasswordResetEmailHtml(userName, resetLink, resetToken);
        log.info("HTML content length: {} chars", htmlContent.length());

        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromAddress)
                    .to(toEmail)
                    .subject("Reset Your Ledgera Password")
                    .html(htmlContent)
                    .build();

            log.info("CreateEmailOptions built successfully, calling Resend API...");
            CreateEmailResponse response = resend.emails().send(params);
            log.info("=== EMAIL SEND SUCCESS === Email ID: {}", response.getId());

        } catch (ResendException e) {
            log.error("=== EMAIL SEND FAILED (ResendException) ===");
            log.error("ResendException message: {}", e.getMessage());
            log.error("ResendException cause: {}", e.getCause() != null ? e.getCause().getMessage() : "none");
            log.error("Full exception:", e);
            throw new RuntimeException("Resend API error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("=== EMAIL SEND FAILED (Unexpected) ===");
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception message: {}", e.getMessage());
            log.error("Full exception:", e);
            throw new RuntimeException("Email send error: " + e.getMessage(), e);
        }
    }

    private String buildPasswordResetEmailHtml(String userName, String resetLink, String resetToken) {
        // Using simple string concatenation to avoid any String.format / %s issues
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"en\">");
        sb.append("<head>");
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        sb.append("<title>Reset Your Password</title>");
        sb.append("<style>");
        sb.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        sb.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', Roboto, 'Helvetica Neue', Arial, sans-serif; line-height: 1.6; color: #1f2937; background-color: #f9fafb; }");
        sb.append(".email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; }");
        sb.append(".header { background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%); padding: 40px 30px; text-align: center; }");
        sb.append(".logo-container { display: inline-block; background-color: rgba(255, 255, 255, 0.15); padding: 16px; border-radius: 16px; margin-bottom: 16px; }");
        sb.append(".header-title { color: #ffffff; font-size: 24px; font-weight: 700; margin: 0; letter-spacing: -0.5px; }");
        sb.append(".content { padding: 40px 30px; }");
        sb.append(".greeting { font-size: 18px; font-weight: 600; color: #111827; margin-bottom: 16px; }");
        sb.append(".message { font-size: 15px; color: #4b5563; margin-bottom: 24px; line-height: 1.7; }");
        sb.append(".button-container { text-align: center; margin: 32px 0; }");
        sb.append(".reset-button { display: inline-block; background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%); color: #ffffff; text-decoration: none; padding: 16px 32px; border-radius: 12px; font-weight: 600; font-size: 15px; }");
        sb.append(".expiry-notice { background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 16px; margin: 24px 0; border-radius: 8px; }");
        sb.append(".expiry-notice p { font-size: 14px; color: #92400e; margin: 0; }");
        sb.append(".expiry-notice strong { color: #78350f; }");
        sb.append(".fallback-section { margin-top: 32px; padding-top: 24px; border-top: 1px solid #e5e7eb; }");
        sb.append(".fallback-title { font-size: 14px; font-weight: 600; color: #6b7280; margin-bottom: 12px; }");
        sb.append(".fallback-link { font-size: 13px; color: #6366f1; word-break: break-all; text-decoration: none; }");
        sb.append(".token-box { background-color: #f3f4f6; border: 1px solid #e5e7eb; border-radius: 8px; padding: 12px; margin-top: 8px; font-family: 'Courier New', monospace; font-size: 12px; color: #374151; word-break: break-all; }");
        sb.append(".security-notice { margin-top: 24px; padding: 16px; background-color: #f9fafb; border-radius: 8px; }");
        sb.append(".security-notice p { font-size: 13px; color: #6b7280; margin: 0; }");
        sb.append(".footer { background-color: #0f0f14; padding: 32px 30px; text-align: center; }");
        sb.append(".footer-brand { margin-bottom: 16px; }");
        sb.append(".footer-name { color: #ffffff; font-size: 16px; font-weight: 600; }");
        sb.append(".footer-text { font-size: 13px; color: #9ca3af; margin: 8px 0; }");
        sb.append(".footer-links { margin-top: 16px; }");
        sb.append(".footer-link { color: #6366f1; text-decoration: none; font-size: 13px; margin: 0 12px; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div class=\"email-container\">");

        // Header
        sb.append("<div class=\"header\">");
        sb.append("<div class=\"logo-container\">");
        sb.append("<svg width=\"48\" height=\"48\" viewBox=\"0 0 512 512\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">");
        sb.append("<rect width=\"512\" height=\"512\" rx=\"96\" fill=\"white\" fill-opacity=\"0.2\"/>");
        sb.append("<g transform=\"translate(128, 128)\">");
        sb.append("<rect x=\"20\" y=\"20\" width=\"40\" height=\"236\" rx=\"8\" fill=\"white\" opacity=\"0.9\"/>");
        sb.append("<rect x=\"60\" y=\"20\" width=\"196\" height=\"236\" rx=\"12\" fill=\"white\"/>");
        sb.append("<line x1=\"90\" y1=\"70\" x2=\"226\" y2=\"70\" stroke=\"#6366f1\" stroke-width=\"4\" stroke-linecap=\"round\"/>");
        sb.append("<line x1=\"90\" y1=\"110\" x2=\"226\" y2=\"110\" stroke=\"#6366f1\" stroke-width=\"4\" stroke-linecap=\"round\"/>");
        sb.append("<line x1=\"90\" y1=\"150\" x2=\"226\" y2=\"150\" stroke=\"#6366f1\" stroke-width=\"4\" stroke-linecap=\"round\"/>");
        sb.append("<line x1=\"90\" y1=\"190\" x2=\"226\" y2=\"190\" stroke=\"#6366f1\" stroke-width=\"4\" stroke-linecap=\"round\"/>");
        sb.append("</g>");
        sb.append("</svg>");
        sb.append("</div>");
        sb.append("<h1 class=\"header-title\">Ledgera</h1>");
        sb.append("</div>");

        // Content
        sb.append("<div class=\"content\">");
        sb.append("<p class=\"greeting\">Hi ").append(userName).append(",</p>");
        sb.append("<p class=\"message\">We received a request to reset your password for your Ledgera account. Click the button below to create a new password.</p>");

        sb.append("<div class=\"button-container\">");
        sb.append("<a href=\"").append(resetLink).append("\" class=\"reset-button\">Reset Password</a>");
        sb.append("</div>");

        sb.append("<div class=\"expiry-notice\">");
        sb.append("<p><strong>&#9201; This link expires in 15 minutes</strong></p>");
        sb.append("<p>For security reasons, this password reset link will only work once and expires shortly.</p>");
        sb.append("</div>");

        sb.append("<div class=\"fallback-section\">");
        sb.append("<p class=\"fallback-title\">Button not working? Copy and paste this link:</p>");
        sb.append("<a href=\"").append(resetLink).append("\" class=\"fallback-link\">").append(resetLink).append("</a>");
        sb.append("<p class=\"fallback-title\" style=\"margin-top: 16px;\">Or use this reset token:</p>");
        sb.append("<div class=\"token-box\">").append(resetToken).append("</div>");
        sb.append("</div>");

        sb.append("<div class=\"security-notice\">");
        sb.append("<p><strong>&#128274; Security Notice:</strong> If you didn't request this password reset, please ignore this email or contact support if you have concerns about your account security.</p>");
        sb.append("</div>");
        sb.append("</div>");

        // Footer
        sb.append("<div class=\"footer\">");
        sb.append("<div class=\"footer-brand\">");
        sb.append("<span class=\"footer-name\">Ledgera</span>");
        sb.append("</div>");
        sb.append("<p class=\"footer-text\">Modern Finance Workspace</p>");
        sb.append("<p class=\"footer-text\">Track expenses, manage records, and oversee your team.</p>");
        sb.append("<div class=\"footer-links\">");
        sb.append("<a href=\"").append(appBaseUrl).append("\" class=\"footer-link\">Visit Dashboard</a>");
        sb.append("<a href=\"").append(appBaseUrl).append("/login\" class=\"footer-link\">Sign In</a>");
        sb.append("</div>");
        sb.append("<p class=\"footer-text\" style=\"margin-top: 16px;\">");
        sb.append("&copy; ").append(java.time.Year.now().getValue()).append(" Ledgera. All rights reserved.");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}
