# Quick Start Guide - Email Integration

## 🚀 Get Started in 5 Minutes

### Step 1: Get Resend API Key (2 minutes)

1. Go to [resend.com](https://resend.com) and sign up
2. Navigate to [API Keys](https://resend.com/api-keys)
3. Click "Create API Key"
4. Copy your API key (starts with `re_`)

### Step 2: Configure Environment (1 minute)

Edit `backend/.env` and add:

```env
RESEND_API_KEY=re_your_actual_api_key_here
RESEND_FROM_EMAIL=onboarding@resend.dev
RESEND_FROM_NAME=Ledgera
APP_BASE_URL=http://localhost:5173
```

**Note:** For development, you can use `onboarding@resend.dev` as the from email (Resend's test domain).

### Step 3: Start the Application (2 minutes)

**Terminal 1 - Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

### Step 4: Test the Flow

1. Open http://localhost:5173/forgot-password
2. Enter your email address
3. Check your inbox for the reset email
4. Click the reset link
5. Set a new password
6. Done! ✅

## 📧 Testing Tips

### Use Test Email
For development testing, send to: `delivered@resend.dev`
- This email will actually be delivered
- Check the Resend dashboard to see all sent emails

### Check Resend Dashboard
- Go to https://resend.com/emails
- See all sent emails in real-time
- View email content and delivery status
- Debug any issues

## 🎨 What You Get

### Professional Email Template
- ✅ Branded design with your logo
- ✅ Responsive mobile layout
- ✅ Primary CTA button
- ✅ Fallback reset link
- ✅ Security notices
- ✅ 15-minute expiry warning

### Enhanced UI Pages
- ✅ Modern forgot password page
- ✅ Success state with instructions
- ✅ Password strength indicator
- ✅ Real-time validation
- ✅ Smooth animations

### Security Features
- ✅ Rate limiting (3 requests per 15 min)
- ✅ Secure token generation
- ✅ 15-minute token expiry
- ✅ One-time use tokens
- ✅ Email enumeration protection

## 🔧 Common Issues

### Email Not Received?
1. Check spam folder
2. Verify API key in `.env`
3. Check Resend dashboard for errors
4. Ensure backend is running

### Rate Limited?
Wait 15 minutes or adjust rate limit in `RateLimitConfig.java`

### Token Expired?
Tokens expire after 15 minutes. Request a new one.

## 📚 Full Documentation

See `EMAIL_INTEGRATION_SETUP.md` for:
- Complete setup instructions
- Production deployment guide
- Security best practices
- Troubleshooting guide
- API documentation

## 🎯 Next Steps

### For Development
- Test the complete flow
- Customize email template
- Adjust rate limits if needed

### For Production
1. Verify your domain with Resend
2. Update `RESEND_FROM_EMAIL` to your domain
3. Set production `APP_BASE_URL`
4. Use production API key
5. Enable HTTPS

## 💡 Pro Tips

1. **Test Mode:** Use `delivered@resend.dev` for testing
2. **Dashboard:** Monitor all emails in Resend dashboard
3. **Logs:** Check backend logs for detailed error messages
4. **Rate Limits:** Adjust in `RateLimitConfig.java` if needed
5. **Customization:** Edit email template in `EmailService.java`

## 🆘 Need Help?

- Check `EMAIL_INTEGRATION_SETUP.md` for detailed docs
- Review backend logs for errors
- Visit [Resend Docs](https://resend.com/docs)
- Check Resend dashboard for email status

---

**That's it!** You now have a production-ready password reset flow with professional emails. 🎉
