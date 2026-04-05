# Complete Ledgera Deployment Guide - Backend + Frontend

This guide covers deploying both Backend (Backend) and Frontend (Vercel/Render) together.

---

## 🎯 Overview

**Backend**: Spring Boot 3.2.5 (Docker)
- **Options**: Render, Heroku, AWS ECS, etc.
- **Guide**: See `backend/DOCKER.md`

**Frontend**: React 18 + Vite (SPA)
- **Options**: Vercel, Render, Netlify, etc.
- **Guide**: See `frontend/DEPLOYMENT.md`

---

## 🚀 Deployment Order

### Step 1: Deploy Backend First
```bash
# Backend deployment (choose one option)
Option A: Render
  - Go to https://dashboard.render.com
  - Create Web Service
  - Deploy Docker image
  - Note the backend URL (e.g., https://ledgera-backend.onrender.com)

Option B: AWS/Heroku/Other
  - Follow platform-specific Docker deployment
  - Note the backend URL
```

### Step 2: Deploy Frontend
```bash
# Frontend deployment (choose one option)
Option A: Vercel
  - Go to https://vercel.com
  - Import GitHub repository
  - Root Directory: frontend
  - Set VITE_API_URL = your-backend-url
  - Deploy

Option B: Render
  - Go to https://dashboard.render.com
  - Create Web Service
  - Root: frontend
  - Build: npm run build
  - Start: npm run preview
  - Set VITE_API_URL = your-backend-url
  - Deploy
```

---

## 🔗 Connecting Frontend to Backend

### Backend URL Format
```
https://ledgera-backend-render.onrender.com
https://your-app-backend.herokuapp.com
https://backend.your-domain.com
```

### Frontend Configuration

**Step 1: Get Backend URL**
- After backend deployment, note the URL
- Example: `https://ledgera-backend.onrender.com`

**Step 2: Set in Frontend**

Vercel:
```
1. Go to Project Settings
2. Environment Variables
3. Add VITE_API_URL = https://ledgera-backend.onrender.com
4. Save and redeploy
```

Render:
```
1. Go to Web Service → Environment
2. Add VITE_API_URL = https://ledgera-backend.onrender.com
3. Save (auto-redeploys)
```

**Step 3: Verify Connection**
- Open frontend in browser
- Open DevTools Console
- Try to login (should attempt API call)
- Check Network tab to see API requests

---

## ✅ Integration Checklist

### Backend
- [ ] Dockerfile created and tested
- [ ] Docker image builds without errors
- [ ] Container runs locally
- [ ] Environment variables configured
- [ ] Database connection works
- [ ] Health endpoint responds
- [ ] Deployed to cloud platform
- [ ] Backend URL accessible from browser

### Frontend
- [ ] npm run build succeeds locally
- [ ] dist/ directory created
- [ ] vercel.json configured
- [ ] Environment template ready
- [ ] Vite config optimized
- [ ] Deployed to cloud platform
- [ ] Frontend URL accessible

### Integration
- [ ] Backend API endpoints respond
- [ ] Frontend loads without errors
- [ ] VITE_API_URL points to backend
- [ ] API requests reach backend
- [ ] Authentication works (login/logout)
- [ ] Data displays correctly
- [ ] No CORS errors
- [ ] No 404 errors on assets

---

## 🔍 Verification Steps

### 1. Test Backend Directly
```bash
# Test backend health endpoint
curl https://your-backend-url.com/api/health
# Should return 200 OK

# Test authentication endpoint
curl -X POST https://your-backend-url.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

### 2. Test Frontend Loads
```bash
# Open in browser
https://your-frontend-url.com

# Check console (F12)
# Should show no errors

# Check Network tab
# Should see API requests to backend
```

### 3. Test Login Flow
```
1. Go to login page
2. Enter test credentials
3. Click login
4. Check Network tab - should see POST to /api/auth/login
5. Should redirect to dashboard on success
```

### 4. Check API Connectivity
```bash
# From browser console
const response = await fetch(
  'https://your-backend-url.com/api/records',
  {
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('ledgera_token')
    }
  }
);
console.log(await response.json());
```

---

## 🐛 Common Issues & Solutions

### Issue: Blank Page on Frontend
**Cause**: Frontend loaded but JavaScript error
**Solution**:
1. Open browser DevTools (F12)
2. Check Console tab for errors
3. Check Network tab for failed requests
4. Look for CORS errors

### Issue: API Requests Return 404
**Cause**: Backend URL incorrect in VITE_API_URL
**Solution**:
1. Check VITE_API_URL is set in platform dashboard
2. Test backend URL directly: `curl https://backend-url/api/health`
3. Verify URL format (no trailing slash): `https://domain.com`
4. Redeploy frontend after fixing

### Issue: CORS Error
**Cause**: Backend CORS not configured for frontend URL
**Solution**:
1. Backend needs CORS header:
   `Access-Control-Allow-Origin: https://your-frontend.vercel.app`
2. Update backend configuration
3. Redeploy backend
4. Test again

### Issue: Login Fails
**Cause**: Could be API, database, or credentials
**Solution**:
1. Check backend logs for errors
2. Test API directly: POST to /api/auth/login
3. Verify database has test user
4. Check JWT_SECRET is set on backend

### Issue: Slow Page Loads
**Cause**: Network latency or backend lag
**Solution**:
1. Check backend performance
2. Monitor backend resource usage
3. Check database connection pool
4. Consider caching if needed

---

## 📊 Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Internet / Users                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
        ┌─────────────────────────────────────────┐
        │          CDN (Global Edge Nodes)        │
        │     (Vercel/Render Auto-handles)        │
        └─────────────────────────────────────────┘
                  ↓                          ↓
    ┌──────────────────────┐    ┌──────────────────────┐
    │   Frontend (SPA)     │    │   Backend (API)      │
    │                      │    │                      │
    │ React + Vite         │    │ Spring Boot          │
    │ Deployed: Vercel     │    │ Deployed: Render     │
    │ URL: vercel.app      │    │ URL: onrender.com    │
    │ Size: ~300KB gzip    │    │ Size: ~450MB Docker  │
    └──────────────────────┘    └──────────────────────┘
            │                              │
            └──────────────────┬───────────┘
                               │
                        HTTPS API Calls
                  (VITE_API_URL environment var)
                               │
                               ↓
                    ┌──────────────────┐
                    │   PostgreSQL DB  │
                    │  (Data Storage)  │
                    └──────────────────┘
```

---

## 🎯 Recommended Setup

### Best Configuration for Ledgera

**Frontend**: 
- **Platform**: Vercel
- **Reason**: Better PR preview URLs, excellent UX
- **Cost**: Free tier sufficient
- **Build**: npm run build
- **Deploy**: Automatic on git push

**Backend**:
- **Platform**: Render
- **Reason**: Good free tier, Docker-native
- **Database**: Render PostgreSQL (optional) or external
- **Cost**: Free tier sufficient for testing
- **Build**: Docker image auto-pushed to registry

**Database**:
- **Option 1**: Render PostgreSQL service (easy)
- **Option 2**: External database (AWS RDS, etc.)
- **Option 3**: Docker Postgres (local only)

---

## 🔐 Security Checklist

- [ ] Backend JWT_SECRET is strong (256+ bits)
- [ ] Frontend doesn't expose secrets in code
- [ ] VITE_API_URL is only variable with backend URL
- [ ] Both deployments use HTTPS only
- [ ] Backend has CORS configured for frontend URL
- [ ] Database credentials not in source code
- [ ] Environment variables set in platform (not in Git)
- [ ] .env files in .gitignore
- [ ] No API keys in public repository

---

## 📈 Monitoring & Maintenance

### Backend Monitoring
- Check Render/platform logs regularly
- Monitor database connection health
- Check API response times
- Set up error alerts

### Frontend Monitoring
- Monitor page load times
- Check for JavaScript errors
- Monitor API call success rates
- Set up deployment failure alerts

### Database Monitoring
- Monitor disk space
- Monitor connection count
- Monitor query performance
- Set up backup alerts

---

## 🚀 Production Deployment Checklist

### Pre-Deployment
- [ ] All features tested locally
- [ ] Backend API endpoints working
- [ ] Frontend builds successfully
- [ ] Environment variables documented
- [ ] Database migrations run
- [ ] API security configured (CORS, rate limiting)
- [ ] Frontend assets optimized
- [ ] SSL certificates configured

### Deployment
- [ ] Backend deployed and tested
- [ ] Frontend deployed with correct API URL
- [ ] Both services accessible from internet
- [ ] API connectivity verified
- [ ] Authentication flow tested
- [ ] All major features tested
- [ ] Monitoring/alerts configured

### Post-Deployment
- [ ] Monitor logs for errors
- [ ] Test all user flows
- [ ] Check performance metrics
- [ ] Document any issues
- [ ] Set up backup/restore procedures
- [ ] Plan for scaling if needed

---

## 📱 Testing Scenarios

### Login Flow
```
1. Go to https://frontend-url.com/login
2. Enter valid credentials
3. Should POST to backend /api/auth/login
4. Should receive JWT token
5. Should redirect to /dashboard
6. Should show user data
```

### Create Record
```
1. Go to /records or /dashboard
2. Create new financial record
3. Should POST to backend /api/records
4. Record should appear in list
5. Should calculate totals correctly
```

### View Dashboard
```
1. Go to /dashboard
2. Should show:
   - Total income
   - Total expenses
   - Net balance
   - Charts/graphs
   - Recent transactions
```

### Logout
```
1. Click logout button
2. Should clear JWT token
3. Should redirect to /login
4. Should not allow access to protected routes
```

---

## 💾 Data Management

### Database Backup
```bash
# Render PostgreSQL automatically backs up
# Manual backup (if using external DB):
pg_dump ledgera_db > backup.sql
```

### Database Migration
```bash
# Flyway handles migrations automatically
# Check backend/src/main/resources/db/migration/
# Add new V{n}__description.sql files as needed
```

---

## 🎉 Success Criteria

✅ Frontend loads without errors
✅ API calls reach backend successfully
✅ Login/authentication works
✅ Create/read/update/delete operations work
✅ Dashboard displays correct data
✅ Charts render properly
✅ Responsive on mobile devices
✅ No console errors
✅ No CORS errors
✅ Page loads in < 3 seconds

---

## 📚 Reference Documents

| Document | Purpose |
|----------|---------|
| backend/DOCKER.md | Backend Docker deployment |
| backend/README-DOCKER.md | Backend overview |
| frontend/DEPLOYMENT.md | Frontend deployment |
| frontend/VERCEL.md | Vercel-specific guide |
| frontend/render.md | Render-specific guide |
| frontend/QUICK-DEPLOY.md | 60-second deployment |

---

## 🆘 Support

**Backend Issues**: See `backend/DOCKER.md` → Troubleshooting
**Frontend Issues**: See `frontend/DEPLOYMENT.md` → Troubleshooting
**Integration Issues**: Check API connectivity with curl
**Platform Issues**: Check respective platform documentation

---

## 🎓 Deployment Timeline

**First Time Deployment**
- Hour 1-2: Deploy and configure backend
- Hour 2-3: Deploy and configure frontend
- Hour 3-4: Test integration
- Hour 4: Production ready

**Subsequent Deployments**
- Minutes: Push to GitHub
- Minutes: Automatic build and deploy
- Minutes: Verify in browser

---

## 🏁 Final Steps

1. **Deploy Backend**
   - Follow backend/DOCKER.md
   - Save backend URL

2. **Deploy Frontend**
   - Follow frontend/DEPLOYMENT.md
   - Set VITE_API_URL = backend URL
   - Deploy

3. **Verify Integration**
   - Open frontend app
   - Test login
   - Test API calls

4. **Monitor**
   - Check logs daily
   - Monitor performance
   - Fix issues promptly

---

**Status**: ✅ Ready for Full Stack Deployment

Both backend and frontend are production-ready. Follow this guide to deploy together!

---

Created: April 5, 2026
Last Updated: April 5, 2026

