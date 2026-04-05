# ✅ Ledgera Deployment Checklist

## Pre-Deployment (Do This First!)

- [ ] Read FULL_DEPLOYMENT.md (top-level project file)
- [ ] Create GitHub account (if not already)
- [ ] Create Render account (https://render.com)
- [ ] Create Vercel account (https://vercel.com)
- [ ] Push code to GitHub

## Backend Deployment (Deploy First!)

### Prepare
- [ ] Note your backend database URL
- [ ] Generate JWT_SECRET (openssl rand -base64 32)
- [ ] Test locally: docker-compose up -d

### Deploy to Render
- [ ] Go to https://dashboard.render.com
- [ ] Click "New+" → "Web Service"
- [ ] Connect GitHub account
- [ ] Select repository
- [ ] Set Root Directory: backend
- [ ] Set Environment Variables:
  - [ ] SPRING_DATASOURCE_URL
  - [ ] SPRING_DATASOURCE_USERNAME
  - [ ] SPRING_DATASOURCE_PASSWORD
  - [ ] JWT_SECRET
- [ ] Click "Create Web Service"
- [ ] Wait for deployment
- [ ] Test: curl https://your-backend-url/api/health
- [ ] **SAVE YOUR BACKEND URL** ← Important!

### Verify Backend
- [ ] Backend URL is accessible
- [ ] Health endpoint responds (200 OK)
- [ ] No errors in Render logs
- [ ] Database connection works

## Frontend Deployment (Deploy Second!)

### Prepare
- [ ] Have backend URL from above
- [ ] Test locally: npm run build && npm run preview
- [ ] Verify dist/ directory created

### Deploy to Vercel
- [ ] Go to https://vercel.com/dashboard
- [ ] Click "Add New..." → "Project"
- [ ] Select your GitHub repository
- [ ] Set Root Directory: frontend
- [ ] Click "Deploy"
- [ ] Wait for deployment
- [ ] Go to Settings → Environment Variables
- [ ] Add VITE_API_URL = your-backend-url
- [ ] Save and Redeploy
- [ ] Get frontend URL
- [ ] **SAVE YOUR FRONTEND URL** ← Done!

### Verify Frontend
- [ ] Frontend URL loads in browser
- [ ] No JavaScript errors in console (F12)
- [ ] Page appears correctly styled
- [ ] No CORS errors

## Integration Testing

### Test API Connectivity
- [ ] Open browser DevTools (F12)
- [ ] Go to Network tab
- [ ] Try login
- [ ] Should see POST to /api/auth/login
- [ ] Should return 200 OK or 401 (invalid creds)
- [ ] Should NOT see CORS error

### Test Core Features
- [ ] Login page loads
- [ ] Dashboard loads (if logged in)
- [ ] Can view records page
- [ ] Page navigation works
- [ ] Logout works
- [ ] Styling looks good
- [ ] Mobile responsive

### Test API Endpoints
- [ ] GET /api/health → 200 OK
- [ ] POST /api/auth/login → Works (with valid creds)
- [ ] GET /api/records → 200 OK (if authenticated)
- [ ] No CORS errors

## Production Verification

- [ ] Frontend loads from Vercel
- [ ] Backend accessible from frontend
- [ ] VITE_API_URL set correctly
- [ ] No hardcoded URLs in code
- [ ] Secrets in environment variables only
- [ ] HTTPS enabled (auto by both platforms)
- [ ] No errors in browser console
- [ ] No errors in backend logs
- [ ] Performance acceptable

## Documentation Review

- [ ] Read backend/DOCKER.md (if deploying backend)
- [ ] Read frontend/DEPLOYMENT.md (if deploying frontend)
- [ ] Understand architecture from FULL_DEPLOYMENT.md
- [ ] Know where to find troubleshooting guides

## Post-Deployment

- [ ] Monitor logs for first few hours
- [ ] Test all major features
- [ ] Check performance
- [ ] Note any issues
- [ ] Plan for monitoring/alerts
- [ ] Consider custom domain (optional)

## Maintenance

- [ ] Set up monitoring alerts
- [ ] Plan backup strategy
- [ ] Document deployment process
- [ ] Plan update process
- [ ] Monitor costs (should be free)

---

## Quick Links

- **Render Dashboard**: https://dashboard.render.com
- **Vercel Dashboard**: https://vercel.com/dashboard
- **Backend Guide**: backend/DOCKER.md
- **Frontend Guide**: frontend/DEPLOYMENT.md
- **Full Guide**: FULL_DEPLOYMENT.md (project root)

---

## Need Help?

**Backend Issues**:
→ backend/DOCKER.md → Troubleshooting

**Frontend Issues**:
→ frontend/DEPLOYMENT.md → Troubleshooting

**Integration Issues**:
→ FULL_DEPLOYMENT.md → Common Issues & Solutions

---

## Estimated Timeline

- Backend deployment: 10-20 minutes (setup) + 5-10 minutes (build)
- Frontend deployment: 10-20 minutes (setup) + 3-5 minutes (build)
- Integration testing: 10-15 minutes
- Total time: 45 minutes to 1 hour

---

## Success Criteria

✅ Backend URL accessible and responding
✅ Frontend URL loads without errors
✅ API requests succeed (check Network tab)
✅ Can navigate the app
✅ No console errors
✅ No CORS errors
✅ Basic features work (login, view data, etc.)

---

**Status**: Ready to Deploy!

Start with backend deployment → then frontend → then test integration.

See FULL_DEPLOYMENT.md for detailed step-by-step instructions.

