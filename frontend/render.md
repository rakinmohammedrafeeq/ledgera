# Render Configuration for Ledgera Frontend

This file configures the Ledgera Frontend for deployment on Render.com

## Deployment Instructions

### Prerequisites
- GitHub repository with your code
- Render.com account

### Step 1: Connect Repository
1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Select the repository containing Ledgera

### Step 2: Configure Service
- **Name**: `ledgera-frontend`
- **Root Directory**: `frontend` (if monorepo)
- **Environment**: `Node`
- **Build Command**: `npm run build`
- **Start Command**: `npm run preview` (or use `npx vite preview --host 0.0.0.0`)

### Step 3: Environment Variables
Add these environment variables in Render dashboard:

```env
# Backend API URL (replace with your actual backend URL)
VITE_API_URL=https://ledgera-backend.render.com
# or for Render preview/development:
VITE_API_URL=http://localhost:8080
```

### Step 4: Deploy
- Click "Create Web Service"
- Render will automatically deploy on push to main branch
- Monitor deployment in Render dashboard

### Important Notes

**Node Version**: Render uses Node 18+ by default (compatible with our setup)

**Build Optimization**: 
- Vite builds to `dist/` directory
- Render automatically serves this directory
- SPA routing handled by native Render support or rewrite rules

**Custom Domain** (Optional):
1. In Render dashboard, go to your service
2. Click "Settings" → "Custom Domain"
3. Add your domain and follow DNS instructions

---

## Automatic Deployments

Render automatically redeploys when you push to the connected branch (default: `main`)

```bash
git push origin main
# Automatically triggers Render build and deployment
```

---

## Troubleshooting

### Build Fails
```bash
# Check logs in Render dashboard
# Verify npm install succeeds locally: npm install
# Check Node version: node --version (should be 16+)
```

### App shows blank page
```bash
# Check browser console for errors
# Verify VITE_API_URL is set correctly
# Check network tab to see API requests
```

### API requests fail
```bash
# Ensure backend is accessible at VITE_API_URL
# Check CORS configuration on backend
# Verify environment variable is set correctly
```

### Static assets 404
```bash
# Verify dist/ directory is created
# Check vite.config.js base configuration
# Ensure public/ directory files are included
```

---

## Performance Optimization

Render tips for better performance:
- Use Render's CDN (auto-enabled)
- Enable gzip compression (auto-enabled)
- Minimize bundle size (Vite handles this)
- Use lazy loading for routes (React Router)

---

## Monitoring

Monitor your Render deployment:
- **Logs**: Real-time logs in dashboard
- **Metrics**: CPU, memory, network usage
- **Alerts**: Set up email alerts for failures

---

For more info: https://render.com/docs

