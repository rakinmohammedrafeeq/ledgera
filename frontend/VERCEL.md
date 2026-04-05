# Vercel Configuration for Ledgera Frontend

This file configures the Ledgera Frontend for deployment on Vercel.

## Deployment Instructions

### Prerequisites
- GitHub repository with your code
- Vercel account (free tier works great)

### Step 1: Connect Repository
1. Go to [Vercel Dashboard](https://vercel.com/dashboard)
2. Click "Add New..." → "Project"
3. Import your Git repository
4. Select the repository with Ledgera

### Step 2: Configure Project
Vercel auto-detects Vite projects! But ensure these settings:

**Root Directory**: `frontend` (if monorepo, required)
**Framework Preset**: `Vite`
**Build Command**: `npm run build`
**Output Directory**: `dist`
**Install Command**: `npm install`

### Step 3: Environment Variables
Add these in Vercel Project Settings → Environment Variables:

```
VITE_API_URL=https://ledgera-backend-render.onrender.com
```

Or for local development (not needed in Vercel):
```
VITE_API_URL=http://localhost:8080
```

**Node Version**: Recommended 18+ (set in `.nvmrc` file if needed)

### Step 4: Deploy
- Click "Deploy"
- Vercel builds and deploys automatically
- Monitor deployment in Vercel dashboard
- Get automatic preview URLs for each PR

### Step 5: Connect Custom Domain (Optional)
1. In Vercel dashboard, go to your project
2. Click "Settings" → "Domains"
3. Add your custom domain
4. Update DNS records following Vercel's instructions

---

## Automatic Deployments

Vercel auto-deploys on:
- Push to `main` branch (production)
- Pull requests (preview deployments)

Deployment workflow:
```bash
# Development
git checkout -b feature/my-feature
git push origin feature/my-feature
# → Vercel creates preview URL

# Review & merge
git checkout main
git merge feature/my-feature
git push origin main
# → Vercel deploys to production
```

---

## Environment Variables Setup

### Development (Local)
Create `.env.local` in frontend/:
```env
VITE_API_URL=http://localhost:8080
```

### Production (Vercel)
Set in Vercel dashboard → Settings → Environment Variables:
```env
VITE_API_URL=https://your-backend-url.com
```

### Staging (Optional)
Create `vercel.json` for multi-environment:
```json
{
  "env": {
    "VITE_API_URL": "@vite_api_url"
  }
}
```

Then use `vercel env pull` to sync.

---

## Build Configuration

### Default Vite Config
Vercel auto-detects `vite.config.js` and uses these settings:

```javascript
// From frontend/vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

This works perfectly with Vercel (proxy only used locally).

### Production Build
```bash
npm run build
# Generates optimized dist/ folder
# Vercel serves this automatically
```

---

## Troubleshooting

### Build fails with "command not found"
**Solution**: Ensure `npm install` completes successfully
```bash
# Verify locally
npm install
npm run build
# Should complete without errors
```

### Environment variables not loaded
**Solution**: Variables must start with `VITE_` for Vite
```javascript
// ✅ Correct - accessible in browser
process.env.VITE_API_URL

// ❌ Incorrect - not accessible in browser
process.env.API_URL
```

### API requests fail with CORS error
**Solution**: Ensure backend allows your Vercel domain
```bash
# Backend should have CORS configured for:
https://your-frontend.vercel.app
```

### Blank page or 404
**Solution**: This is a SPA routing issue. The `vercel.json` rewrites handle it.
```bash
# Check that vercel.json exists with routing configuration
# Verify dist/ directory has index.html
```

### Static assets return 404
**Solution**: Check Vite build configuration
```bash
# Verify public/ folder files are included
# Check vite.config.js base setting
# Run: npm run build && npm run preview
```

### Deployment takes too long
**Solution**: 
- Clear Vercel cache: Settings → Git → Clear Build Cache
- Check node_modules size
- Review build output for optimizations

---

## Performance Optimization

Vercel features that help:
- **Edge Network**: Auto-optimized globally
- **Image Optimization**: Vercel auto-optimizes images
- **Serverless Functions**: Available if needed
- **ISR**: Incremental Static Regeneration (for static sites)

Our React SPA benefits from:
- **Gzip compression**: Auto-enabled
- **Minification**: Built into Vite
- **Code splitting**: React Router handles this
- **Lazy loading**: Implemented in components

---

## Monitoring & Analytics

**Vercel Dashboard**:
- Real-time deployment status
- Build logs and errors
- Performance metrics
- Analytics (with Vercel Analytics)

Enable Vercel Analytics:
```javascript
// In src/main.jsx
import { Analytics } from '@vercel/analytics/react';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      {/* ... */}
      <Analytics />
    </BrowserRouter>
  </React.StrictMode>
);
```

Then install: `npm install @vercel/analytics`

---

## Rollback to Previous Deployment

If something goes wrong:
1. Go to Vercel dashboard
2. Click your project
3. Go to "Deployments"
4. Find the previous working deployment
5. Click "..." menu → "Promote to Production"

---

## Preview Deployments

Every pull request gets a preview URL:
1. Push to GitHub with PR
2. Vercel automatically creates preview deployment
3. Share preview URL in PR comments
4. Test before merging to main

Preview URL format: `https://ledgera-pr-123.vercel.app`

---

## Custom Configuration

### .nvmrc (Optional - Pin Node Version)
Create `.nvmrc` in project root:
```
18
```
This ensures Vercel uses Node 18.

### vercel.json (Advanced)
Already created with build configuration. Modify if needed:
```json
{
  "buildCommand": "npm run build",
  "devCommand": "npm run dev",
  "installCommand": "npm install",
  "framework": "vite",
  "outputDirectory": "dist"
}
```

---

## Security

**Environment Variables**:
- Never commit `.env.local` to Git
- Add to `.gitignore` (already done)
- Set sensitive values in Vercel dashboard only

**API Calls**:
- All API calls go through `/api` proxy in dev
- In production, configure backend CORS headers
- Use HTTPS only (Vercel auto-enforces)

---

## Cost

Vercel pricing:
- **Free tier**: Sufficient for most projects
  - Unlimited deployments
  - Serverless Functions (with limits)
  - Bandwidth: 100GB/month
  - Edge locations globally
  
- **Pro**: $20/month (if needed)
  - Increased limits
  - Priority support

---

## Common Commands

```bash
# Install Vercel CLI (optional)
npm install -g vercel

# Deploy from CLI
vercel deploy

# Deploy to production
vercel deploy --prod

# View logs
vercel logs <project-name>

# Pull environment variables
vercel env pull

# Link to existing project
vercel link
```

---

## Next Steps

1. ✅ Create `vercel.json` (already done)
2. ✅ Set up environment variables in Vercel dashboard
3. ✅ Push to GitHub
4. ✅ Import project in Vercel
5. ✅ Verify deployment works
6. ✅ Add custom domain (optional)

---

## Support

- **Vercel Docs**: https://vercel.com/docs
- **Vite Docs**: https://vitejs.dev
- **React Router Docs**: https://reactrouter.com

---

**Status**: ✅ Ready for Vercel Deployment

See `vercel.json` for configuration.

