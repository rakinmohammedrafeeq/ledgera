# Render Deployment Checklist - OTP Email Issue

## Required Environment Variables on Render

Go to Render Dashboard → Your Service → Environment → Add Environment Variable

### 1. Database Configuration
```
DB_URL=jdbc:postgresql://your-neon-host/your-database?sslmode=require
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### 2. JWT Configuration
```
JWT_SECRET=your-secret-key-at-least-256-bits-long
JWT_EXPIRATION=86400000
```

### 3. Resend Email Configuration (CRITICAL FOR OTP)
```
RESEND_API_KEY=re_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
RESEND_FROM_EMAIL=onboarding@resend.dev
RESEND_FROM_NAME=Ledgera
```

**Important Notes:**
- `RESEND_API_KEY` must start with `re_`
- Get your API key from: https://resend.com/api-keys
- For testing, use `onboarding@resend.dev` as sender
- For production, verify your domain and use your own email

### 4. Application Configuration
```
APP_BASE_URL=https://ledgera-finance-system.vercel.app
LEDGERA_SEED_ADMIN=true
```

## Verification Steps

### Step 1: Check Environment Variables
1. Go to Render Dashboard
2. Click on your backend service
3. Go to "Environment" tab
4. Verify ALL variables above are set
5. Check for:
   - No extra spaces
   - No quotes around values
   - Correct spelling
   - Valid values

### Step 2: Verify Resend Configuration

#### Check Resend Dashboard
1. Go to https://resend.com/api-keys
2. Verify your API key is active
3. Check API key permissions (should have "Sending access")
4. Verify sender email/domain:
   - For testing: `onboarding@resend.dev` (no verification needed)
   - For production: Your verified domain

#### Test Resend API Key
```bash
curl -X POST 'https://api.resend.com/emails' \
  -H 'Authorization: Bearer YOUR_API_KEY' \
  -H 'Content-Type: application/json' \
  -d '{
    "from": "onboarding@resend.dev",
    "to": "your-email@example.com",
    "subject": "Test Email",
    "html": "<p>Test</p>"
  }'
```

Expected response:
```json
{
  "id": "email_id_here"
}
```

### Step 3: Check Backend Logs on Render

1. Go to Render Dashboard → Your Service → Logs
2. Look for these log messages on startup:

```
=== RESEND CONFIG INIT ===
API Key present: true
API Key length: 40
API Key prefix: re_...
=== RESEND CONFIG OK ===
```

**If you see:**
- `API Key present: false` → RESEND_API_KEY not set
- `API Key length: 0` → RESEND_API_KEY is empty
- `RESEND_API_KEY is not configured properly!` → Invalid or missing key

### Step 4: Test OTP Request

1. Try requesting OTP from frontend
2. Check Render logs for detailed step-by-step output:

```
=== OTP REQUEST START ===
Email: user@example.com
Step 1: Rate limiting check
Step 1 PASSED: Rate limit check successful
Step 2: Finding user by email
Step 2 PASSED: User found - ID: 1, Name: John
Step 3: Checking resend cooldown
Step 3 PASSED: No active cooldown
Step 4: Generating OTP
Step 4 PASSED: OTP generated successfully
Step 5: Saving OTP to database
Step 5 PASSED: OTP saved to database successfully
Step 6: Preparing to send OTP email
Step 6a: Calling emailService.sendOtpEmail()
=== OTP EMAIL START ===
To: user@example.com
OTP: 123456
Calling Resend API...
=== OTP EMAIL SEND SUCCESS === Email ID: xxx
Step 6 PASSED: OTP email sent successfully
=== OTP REQUEST SUCCESS ===
```

**If logs stop at a specific step, that's where the issue is.**

### Step 5: Common Issues and Solutions

#### Issue: Logs stop at "Step 6a: Calling emailService.sendOtpEmail()"
**Cause:** Resend API call is hanging or failing
**Solutions:**
1. Check RESEND_API_KEY is valid
2. Check network connectivity from Render
3. Verify Resend service status: https://resend.com/status
4. Check Resend API limits: https://resend.com/pricing

#### Issue: "ResendException" in logs
**Cause:** Resend API rejected the request
**Solutions:**
1. Check sender email is valid
2. Verify API key has sending permissions
3. Check Resend dashboard for error details
4. Ensure you're not exceeding rate limits

#### Issue: "API Key present: false"
**Cause:** RESEND_API_KEY environment variable not set
**Solutions:**
1. Add RESEND_API_KEY in Render environment variables
2. Restart the service after adding
3. Wait for deployment to complete

#### Issue: Rate limit error
**Cause:** Too many OTP requests (10 per 15 minutes)
**Solutions:**
1. Wait 15 minutes
2. Use a different email
3. Restart backend service to clear rate limits (development only)

## Deployment Process

### After Changing Environment Variables:
1. Save the environment variable in Render
2. Render will automatically redeploy
3. Wait for deployment to complete (2-3 minutes)
4. Check logs for "RESEND CONFIG OK"
5. Test OTP request again

### Manual Redeploy:
1. Go to Render Dashboard → Your Service
2. Click "Manual Deploy" → "Deploy latest commit"
3. Wait for deployment
4. Check logs

## Testing Checklist

- [ ] All environment variables set on Render
- [ ] RESEND_API_KEY starts with `re_`
- [ ] Backend logs show "RESEND CONFIG OK"
- [ ] Backend is running (health check passes)
- [ ] Frontend can reach backend
- [ ] OTP request shows all steps in logs
- [ ] Email is received in inbox
- [ ] OTP code works for password reset

## Support

If OTP emails still don't work after following this checklist:

1. **Check Render Logs** - Copy the full log output from an OTP request
2. **Check Resend Dashboard** - Go to https://resend.com/emails to see if emails were sent
3. **Verify API Key** - Test the API key with curl command above
4. **Check Email Spam** - OTP emails might be in spam folder

## Production Recommendations

1. **Use Custom Domain for Emails**
   - Verify your domain in Resend
   - Use `noreply@yourdomain.com` instead of `onboarding@resend.dev`
   - Better deliverability and branding

2. **Monitor Email Sending**
   - Set up Resend webhooks for delivery tracking
   - Monitor Resend dashboard for failures
   - Set up alerts for email failures

3. **Rate Limiting**
   - Current: 10 requests per 15 minutes per email
   - Adjust in `RateLimitConfig.java` if needed
   - Consider IP-based rate limiting for production

4. **Error Handling**
   - All errors are logged with full stack traces
   - Users see friendly error messages
   - Failed OTPs are rolled back from database

---

**Last Updated:** 2026-05-10
