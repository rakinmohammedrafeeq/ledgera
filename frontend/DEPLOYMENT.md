# Frontend Deployment Guide - Vercel & Render

This guide covers deploying the Ledgera Frontend to Vercel and/or Render.

## 📋 Prerequisites

- Frontend source code in `frontend/` directory
- Node.js 18+ installed locally
- Git repository pushed to GitHub
- Backend deployed (Render or other platform)

---

## 🚀 Quick Start (3 Steps)

### Step 1: Build Locally
```bash
cd frontend
npm install
npm run build
```

### Step 2: Deploy to Vercel
```bash
# Option A: Using Vercel Dashboard
# 1. Go to https://vercel.com
# 2. Click "Add New Project"
# 3. Import your GitHub repository
# 4. Root Directory: frontend
# 5. Click "Deploy"

# Option B: Using Vercel CLI
npm install -g vercel
vercel deploy --prod
```

### Step 3: Deploy to Render
```bash
# 1. Go to https://dashboard.render.com
# 2. Click "New+" → "Web Service"
# 3. Connect GitHub repository
# 4. Root Directory: frontend
# 5. Build Command: npm run build
# 6. Start Command: npm run preview
# 7. Click "Create Web Service"
```

---

## 🎯 Platform Comparison

| Feature | Vercel | Render |
|---------|--------|--------|
| **Free Tier** | Yes | Yes |
| **Build Time** | ~1-2 min | ~1-2 min |
| **Global CDN** | Yes | Yes |
| **Auto-Deploy** | Push to main | Push to main |
| **Preview URLs** | Per PR | Per deployment |
| **Domain** | Custom domain | Custom domain |
| **Cost** | Free / $20/mo | Free / $7/mo |

---

## 📁 Files Created

### Deployment Configuration
- **vercel.json** - Vercel build settings
- **render.md** - Render deployment guide
- **VERCEL.md** - Vercel deployment guide

### Environment Setup
- **.env.production** - Production environment variables template
- **.nvmrc** - Node version lock (v18)

### Automation Scripts
- **deploy.sh** - Bash deployment script (Linux/macOS)
- **deploy.bat** - Batch deployment script (Windows)

---

## 🔧 Configuration

### Environment Variables

Set these in your platform dashboard:

```env
# Backend API URL - REQUIRED
VITE_API_URL=https://ledgera-backend-render.onrender.com
```

**Vercel Dashboard**:
1. Go to Settings → Environment Variables
2. Add `VITE_API_URL`
3. Set for Production

**Render Dashboard**:
1. Go to Environment
2. Add `VITE_API_URL`
3. Save and redeploy

---

## 📊 Project Structure

```
frontend/
├── src/
│   ├── main.jsx           React app entry
│   ├── App.jsx            Main component
│   ├── api/
│   │   └── axios.js       API configuration
│   ├── pages/             Page components
│   ├── components/        Reusable components
│   ├── context/           React contexts
│   └── assets/            Static assets
├── public/                Static files
├── dist/                  Build output (created by npm run build)
├── package.json           Dependencies
├── vite.config.js         Vite configuration
├── index.html             HTML entry point
├── vercel.json            ✅ Vercel config
├── render.md              ✅ Render guide
├── VERCEL.md              ✅ Vercel guide
├── .env.production        ✅ Production env template
├── .nvmrc                 ✅ Node version
├── deploy.sh              ✅ Deploy script (Unix)
└── deploy.bat             ✅ Deploy script (Windows)
```

---

## 🌐 Vercel Deployment

### Option 1: Dashboard Deployment (Easiest)

1. **Connect Repository**
   - Go to https://vercel.com/dashboard
   - Click "Add New..." → "Project"
   - Select your GitHub repository

2. **Configure**
   - Root Directory: `frontend`
   - Framework: Auto-detected (Vite)
   - Build: `npm run build`
   - Output: `dist`

3. **Environment Variables**
   - Click "Settings" → "Environment Variables"
   - Add `VITE_API_URL=https://your-backend-url.com`
   - Save

4. **Deploy**
   - Click "Deploy"
   - Watch build progress
   - Get live URL when done

### Option 2: CLI Deployment

```bash
cd frontend

# Install Vercel CLI
npm install -g vercel

# Deploy to preview
vercel deploy

# Deploy to production
vercel deploy --prod

# View logs
vercel logs <project-name>
```

### Vercel Features

- **Auto-Deploy**: Every push to main auto-deploys
- **Preview URLs**: Every PR gets a preview URL
- **Rollbacks**: Easy rollback to previous deployments
- **Analytics**: Built-in performance analytics
- **Serverless**: Functions available (if needed)

---

## 🎨 Render Deployment

### Step-by-Step Guide

1. **Create Web Service**
   - Go to https://dashboard.render.com
   - Click "New+" → "Web Service"
   - Connect GitHub account
   - Select your repository

2. **Configure Service**
   ```
   Name:               ledgera-frontend
   Root Directory:     frontend
   Environment:        Node
   Build Command:      npm run build
   Start Command:      npm run preview
   Plan:               Free
   ```

3. **Add Environment Variables**
   - Go to "Environment"
   - Add key `VITE_API_URL`
   - Value: `https://your-backend-url.com`
   - Save changes

4. **Deploy**
   - Click "Create Web Service"
   - Render builds and deploys automatically
   - Get URL when deployment completes

### Auto-Deploy on Git Push

- Render automatically redeploys when you push to main
- No additional setup needed
- Check deployment status in Render dashboard

---

## ✅ Verification Checklist

### Before Deployment

- [ ] Local build works: `npm run build`
- [ ] dist/ directory created with index.html
- [ ] No build errors or warnings
- [ ] Backend is deployed and accessible
- [ ] Backend URL is ready (for VITE_API_URL)

### After Deployment

- [ ] Frontend loads without errors
- [ ] Can navigate between pages
- [ ] API calls reach backend successfully
- [ ] Authentication works (login/logout)
- [ ] No console errors

### Quick Tests

```bash
# Test local build
npm run build
npm run preview
# Open http://localhost:4173
# Verify app works

# Check API connectivity
curl https://your-backend-url.com/api/health
# Should return 200 OK

# Check frontend build size
ls -lh dist/
# Should see reasonable file sizes
```

---

## 🔗 API Configuration

### Development (Local)
```env
# frontend/.env.local
VITE_API_URL=http://localhost:8080
```

### Production (Vercel)
```
Vercel Dashboard → Settings → Environment Variables
VITE_API_URL=https://ledgera-backend-render.onrender.com
```

### Production (Render)
```
Render Dashboard → Environment
VITE_API_URL=https://ledgera-backend-render.onrender.com
```

### Accessing in Code
```javascript
// Automatically uses VITE_API_URL from environment
const API_URL = import.meta.env.VITE_API_URL || '/api';

// Or directly in axios config
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api'
});
```

---

## 🚨 Troubleshooting

### Build Fails

**Error**: `npm install` fails
- **Solution**: Check Node version: `node --version` (should be 18+)
- Create `.nvmrc` with `18` (already done)

**Error**: `npm run build` fails
- **Solution**: Run locally to debug: `npm run build`
- Check for TypeScript errors in console

### App Shows Blank Page

**Cause**: API URL misconfigured
- **Solution**: Check browser console for errors
- Verify `VITE_API_URL` set in platform dashboard
- Test API with curl

**Cause**: React Router routing issue
- **Solution**: Vercel rewrites handle SPA routing
- Check that index.html is served for all routes

### API Calls Fail (CORS Error)

**Cause**: Backend CORS not configured
- **Solution**: Backend must allow frontend URL
- Example: `Access-Control-Allow-Origin: https://your-frontend.vercel.app`

**Cause**: Wrong API URL in environment
- **Solution**: Verify `VITE_API_URL` matches deployed backend
- Test: `curl https://your-backend-url.com/api/health`

### 404 on Static Assets

**Cause**: Public folder not deployed
- **Solution**: Check `public/` folder in dist/
- Verify vite.config.js settings

**Cause**: Base path issue
- **Solution**: Check vite.config.js for base configuration
- For root deployment, base should be `/`

---

## 📈 Performance Optimization

### Vercel Tips
- Enable Vercel Analytics (see VERCEL.md)
- Use Vercel Image Optimization (if needed)
- Leverage global edge network

### Render Tips
- Use Render's CDN (auto-enabled)
- Gzip compression (auto-enabled)
- Monitor resource usage

### Code Optimization
- Lazy load routes: `React.lazy()`
- Code splitting: React Router handles automatically
- Image optimization: Compress PNGs/JPGs
- Remove unused dependencies

---

## 🎯 Next Steps

1. **Verify backend is deployed**
   - Test: `curl https://backend-url.com/api/health`

2. **Get backend URL**
   - Save for use in VITE_API_URL

3. **Choose platform**
   - Vercel: Easier with dashboard UI
   - Render: Good free tier
   - Deploy to both for redundancy

4. **Deploy frontend**
   - Follow platform-specific guide above
   - Set VITE_API_URL environment variable
   - Test in deployment

5. **Configure custom domain** (optional)
   - Both platforms support custom domains
   - Update DNS records

---

## 📚 Additional Resources

- **Vercel Docs**: https://vercel.com/docs/frameworks/vite
- **Render Docs**: https://render.com/docs/web-services
- **Vite Docs**: https://vitejs.dev/guide/
- **React Router**: https://reactrouter.com/

---

## 💡 Pro Tips

1. **Test locally first**
   ```bash
   npm run build && npm run preview
   ```

2. **Watch deployment logs**
   - Vercel & Render show real-time logs
   - Check for build errors immediately

3. **Use preview deployments**
   - Test in staging before production
   - Both platforms support preview URLs

4. **Set up alerts** (optional)
   - Email notifications on deployment failure
   - Helps catch issues quickly

5. **Monitor after deployment**
   - Check browser console for errors
   - Verify API connectivity
   - Test all major features

---

## 🎉 Status

✅ Frontend ready for Vercel deployment
✅ Frontend ready for Render deployment
✅ Environment configuration templates created
✅ Deployment guides written
✅ Automation scripts provided

**Next Action**: Deploy to your chosen platform!

---

**Created**: April 5, 2026
**Status**: Production-Ready

