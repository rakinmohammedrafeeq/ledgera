# Frontend Deployment Quick Reference

## 🎯 In 60 Seconds

### Build Locally
```bash
cd frontend
npm install && npm run build
```

### Deploy to Vercel
```bash
vercel deploy --prod
# Or use dashboard: https://vercel.com → Import project
```

### Deploy to Render
```
1. Go to https://dashboard.render.com
2. Click "New+" → "Web Service"
3. Connect GitHub repo
4. Root: frontend | Build: npm run build | Start: npm run preview
5. Set VITE_API_URL environment variable
6. Click "Create Web Service"
```

---

## 📋 Deployment Checklist

- [ ] Backend deployed and accessible
- [ ] Backend URL ready (e.g., https://ledgera-backend.onrender.com)
- [ ] Local build successful: `npm run build`
- [ ] Code pushed to GitHub main branch
- [ ] Choose platform (Vercel or Render)
- [ ] Import project in platform dashboard
- [ ] Set VITE_API_URL environment variable
- [ ] Deploy
- [ ] Test app loads and API works

---

## 🌐 Platform URLs

**Vercel**: https://vercel.com/dashboard
**Render**: https://dashboard.render.com

---

## 🔧 Environment Variables

Required: `VITE_API_URL=https://your-backend-url.com`

Set in platform dashboard → Settings/Environment Variables

---

## 📁 Key Files

| File | Purpose |
|------|---------|
| vercel.json | Vercel build config |
| VERCEL.md | Vercel setup guide |
| render.md | Render setup guide |
| DEPLOYMENT.md | Full deployment guide |
| .env.production | Production env template |
| .nvmrc | Node version (v18) |
| deploy.sh | Deploy script (Unix) |
| deploy.bat | Deploy script (Windows) |

---

## ✅ Test Deployment

After deploy, test:

```bash
# 1. Check frontend loads
curl https://your-frontend-url.com

# 2. Check API connectivity
curl https://your-frontend-url.com/api/health

# 3. Test login page loads
# Open https://your-frontend-url.com in browser
```

---

## 📞 Need Help?

- **Setup**: See DEPLOYMENT.md
- **Vercel**: See VERCEL.md or vercel.com/docs
- **Render**: See render.md or render.com/docs
- **Common Issues**: See DEPLOYMENT.md → Troubleshooting

---

## 🚀 You're Ready!

All files configured. Deploy now! 🎉

