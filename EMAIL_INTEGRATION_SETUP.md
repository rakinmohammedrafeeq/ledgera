# Resend Email Integration - Complete Setup Guide

## Overview
This document describes the complete Resend email integration for password reset functionality in the Ledgera application.

## Features Implemented

### Backend
✅ **Resend Email Service Integration**
- Professional HTML email templates with brand styling
- Secure token generation and expiry (15 minutes)
- Rate limiting (3 requests per 15 minutes per email)
- Proper error handling and logging
- Environment-based configuration

✅ **Security Features**
- Secure UUID-based reset tokens
- Token expiry enforcement
- Rate limiting to prevent abuse
- Email enumeration protection
- One-time use tokens

✅ **Email Template Features**
- Premium branded design matching app UI
- Embedded SVG logo
- Responsive mobile-friendly layout
- Primary reset button with fallback link
- Token display for manual entry
- Expiry warning (15 minutes)
- Security notices
- Professional footer with branding

### Frontend
✅ **Enhanced Forgot Password Page**
- Modern, premium UI design
- Success state with step-by-step instructions
- Loading states with branded spinner
- Error handling with descriptive messages
- Email validation
- Accessibility compliant

✅ **Enhanced Reset Password Page**
- Real-time password strength indicator
- Password requirements checklist
- Show/hide password toggle
- Token auto-fill from URL parameter
- Manual token entry option
- Success/error feedback
- Smooth animations and transitions

## Setup Instructions

### 1. Get Resend API Key

1. Sign up at [Resend](https://resend.com)
2. Verify your domain (or use their test domain for development)
3. Generate an API key from the [API Keys page](https://resend.com/api-keys)
4. Copy your API key

### 2. Configure Environment Variables

Update `backend/.env` with your Resend credentials:

```env
# Resend Email Configuration
RESEND_API_KEY=re_your_actual_api_key_here
RESEND_FROM_EMAIL=noreply@yourdomain.com
RESEND_FROM_NAME=Ledgera
APP_BASE_URL=http://localhost:5173
```

**Production Configuration:**
```env
RESEND_API_KEY=re_your_production_api_key
RESEND_FROM_EMAIL=noreply@ledgera.com
RESEND_FROM_NAME=Ledgera
APP_BASE_URL=https://yourdomain.com
```

### 3. Domain Verification (Production)

For production use, you must verify your domain with Resend:

1. Go to [Resend Domains](https://resend.com/domains)
2. Add your domain
3. Add the provided DNS records to your domain
4. Wait for verification (usually a few minutes)
5. Update `RESEND_FROM_EMAIL` to use your verified domain

### 4. Install Dependencies

The required dependencies are already added to `pom.xml`:

```xml
<!-- Resend Email -->
<dependency>
    <groupId>com.resend</groupId>
    <artifactId>resend-java</artifactId>
    <version>3.0.0</version>
</dependency>

<!-- Bucket4j for Rate Limiting -->
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
```

Run Maven to download dependencies:
```bash
cd backend
./mvnw clean install
```

### 5. Test the Integration

#### Development Testing

1. Start the backend:
```bash
cd backend
./mvnw spring-boot:run
```

2. Start the frontend:
```bash
cd frontend
npm run dev
```

3. Test the flow:
   - Navigate to http://localhost:5173/forgot-password
   - Enter a registered email address
   - Check your email inbox for the reset email
   - Click the reset link or copy the token
   - Set a new password
   - Sign in with the new password

#### Using Resend Test Mode

For development, Resend provides a test mode:
- Emails sent to `delivered@resend.dev` will be delivered
- Other emails will be captured but not sent
- Check the Resend dashboard for all sent emails

## File Structure

### Backend Files Created/Modified

```
backend/
├── src/main/java/com/ledgera/
│   ├── config/
│   │   ├── EmailConfig.java          # Resend client configuration
│   │   └── RateLimitConfig.java      # Rate limiting setup
│   ├── service/
│   │   ├── EmailService.java         # Email sending logic & templates
│   │   └── AuthService.java          # Updated with email integration
│   └── ...
├── .env                               # Environment variables (updated)
├── .env.example                       # Example configuration (new)
└── pom.xml                            # Dependencies (updated)
```

### Frontend Files Modified

```
frontend/
└── src/pages/auth/
    ├── ForgotPasswordPage.tsx        # Enhanced UI with success state
    └── ResetPasswordPage.tsx         # Enhanced UI with password strength
```

## Email Template

The email template includes:

### Header
- Gradient background (brand colors)
- Embedded SVG logo
- App name

### Content
- Personalized greeting
- Clear instructions
- Primary CTA button (Reset Password)
- Expiry warning (15 minutes)
- Fallback reset link
- Manual token display
- Security notice

### Footer
- Brand logo and name
- App description
- Quick links (Dashboard, Sign In)
- Copyright notice

### Styling
- Responsive design (mobile-friendly)
- Premium gradient buttons
- Professional color scheme
- Accessible contrast ratios
- Inline CSS for email client compatibility

## Security Features

### Rate Limiting
- **Limit:** 3 password reset requests per 15 minutes per email
- **Implementation:** Bucket4j token bucket algorithm
- **Storage:** In-memory (ConcurrentHashMap)
- **Production Note:** Consider Redis for distributed systems

### Token Security
- **Format:** UUID v4 (cryptographically secure)
- **Expiry:** 15 minutes
- **One-time use:** Token cleared after successful reset
- **Storage:** Database with expiry timestamp

### Email Enumeration Protection
- Generic success message regardless of email existence
- Consistent response time
- No indication if email is registered

### Additional Security
- HTTPS required in production
- Password strength requirements (8+ characters)
- Secure password hashing (BCrypt)
- JWT authentication for API access

## API Endpoints

### POST /api/auth/forgot-password
Request password reset email.

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "If an account exists with this email, you will receive password reset instructions."
}
```

**Error Responses:**
- `400 Bad Request` - Too many requests (rate limit exceeded)
- `404 Not Found` - Email not found (hidden from user)
- `500 Internal Server Error` - Email sending failed

### POST /api/auth/reset-password
Reset password using token.

**Request:**
```json
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "newPassword": "NewSecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password has been reset successfully"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid or expired token
- `400 Bad Request` - Password validation failed

## Testing Checklist

### Functional Testing
- [ ] User receives email after requesting password reset
- [ ] Email contains correct reset link with token
- [ ] Reset link redirects to correct page with token pre-filled
- [ ] Password can be reset successfully with valid token
- [ ] Token expires after 15 minutes
- [ ] Token can only be used once
- [ ] Rate limiting prevents abuse (max 3 requests per 15 min)
- [ ] Invalid tokens show appropriate error
- [ ] Expired tokens show appropriate error

### UI/UX Testing
- [ ] Forgot password page loads correctly
- [ ] Form validation works (email format)
- [ ] Loading states display during submission
- [ ] Success state shows after email sent
- [ ] Reset password page loads correctly
- [ ] Password strength indicator works
- [ ] Password requirements checklist updates in real-time
- [ ] Show/hide password toggle works
- [ ] Success message shows after password reset
- [ ] Redirect to login works after successful reset

### Email Testing
- [ ] Email arrives in inbox (not spam)
- [ ] Email displays correctly in Gmail
- [ ] Email displays correctly in Outlook
- [ ] Email displays correctly on mobile
- [ ] Logo displays correctly
- [ ] Reset button works
- [ ] Fallback link works
- [ ] Token can be copied and used manually

### Security Testing
- [ ] Rate limiting works (try 4 requests quickly)
- [ ] Expired tokens are rejected
- [ ] Used tokens are rejected
- [ ] Invalid tokens are rejected
- [ ] Email enumeration is prevented
- [ ] Password strength requirements enforced

## Troubleshooting

### Email Not Received

1. **Check Resend Dashboard**
   - Go to https://resend.com/emails
   - Check if email was sent successfully
   - Look for any errors

2. **Check Spam Folder**
   - Password reset emails may be flagged as spam
   - Add sender to safe senders list

3. **Verify Domain**
   - Ensure domain is verified in Resend
   - Check DNS records are correct

4. **Check API Key**
   - Verify API key is correct in `.env`
   - Ensure API key has send permissions

5. **Check Logs**
   - Backend logs will show email sending attempts
   - Look for ResendException errors

### Rate Limiting Issues

If users are getting rate limited too quickly:

1. Adjust the rate limit in `RateLimitConfig.java`:
```java
// Change from 3 requests per 15 minutes to 5 requests per 30 minutes
Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(30)));
```

2. For production with multiple servers, use Redis:
```java
// Add Redis dependency and configure distributed rate limiting
```

### Token Expiry Issues

If 15 minutes is too short:

1. Update `AuthService.java`:
```java
// Change from 15 minutes to 30 minutes
user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
```

2. Update email template in `EmailService.java`:
```java
// Update expiry text in email
"This link expires in 30 minutes"
```

## Production Deployment

### Pre-deployment Checklist

- [ ] Verify domain with Resend
- [ ] Update `RESEND_FROM_EMAIL` to verified domain
- [ ] Set production `APP_BASE_URL`
- [ ] Use production Resend API key
- [ ] Enable HTTPS
- [ ] Configure proper CORS settings
- [ ] Set up monitoring/logging
- [ ] Test email delivery in production
- [ ] Configure Redis for distributed rate limiting (if using multiple servers)

### Environment Variables

Ensure these are set in your production environment:

```env
RESEND_API_KEY=re_production_key_here
RESEND_FROM_EMAIL=noreply@yourdomain.com
RESEND_FROM_NAME=Ledgera
APP_BASE_URL=https://yourdomain.com
```

### Monitoring

Monitor these metrics:
- Email delivery rate
- Email bounce rate
- Password reset success rate
- Rate limit hits
- Token expiry rate
- Average time to reset

### Scaling Considerations

For high-traffic applications:
1. Use Redis for distributed rate limiting
2. Consider email queue (RabbitMQ/Kafka)
3. Monitor Resend API rate limits
4. Set up email delivery webhooks
5. Implement retry logic for failed sends

## Cost Considerations

### Resend Pricing (as of 2024)
- **Free Tier:** 100 emails/day, 3,000 emails/month
- **Pro Plan:** $20/month for 50,000 emails
- **Enterprise:** Custom pricing

### Recommendations
- Free tier is sufficient for small applications
- Monitor usage in Resend dashboard
- Set up billing alerts
- Consider email volume in scaling plans

## Support

### Resend Documentation
- [Resend Docs](https://resend.com/docs)
- [Resend API Reference](https://resend.com/docs/api-reference)
- [Resend Support](https://resend.com/support)

### Ledgera Support
- Check application logs for errors
- Review this documentation
- Test in development environment first
- Contact development team for assistance

## Changelog

### Version 1.0.0 (Current)
- Initial Resend integration
- Password reset email flow
- Rate limiting implementation
- Enhanced UI/UX for auth pages
- Professional email template
- Comprehensive documentation

## Future Enhancements

Potential improvements:
- [ ] Welcome email for new users
- [ ] Email verification on registration
- [ ] Two-factor authentication via email
- [ ] Account activity notifications
- [ ] Team invitation emails
- [ ] Monthly financial summary emails
- [ ] Email preferences management
- [ ] Multi-language email templates
- [ ] Email analytics dashboard
- [ ] A/B testing for email templates
