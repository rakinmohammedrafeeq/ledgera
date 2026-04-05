# Quick Reference Card - Ledgera Deployment

## 🎯 Start Here

```
1. Read: START_HERE.md (5 min)
2. Read: DEPLOYMENT_CHECKLIST.md (5 min)
3. Read: FULL_DEPLOYMENT.md (20 min)
4. Deploy backend (30-45 min)
5. Deploy frontend (20-30 min)
6. Test (15 min)
```

## 📋 Key Files

| File | Purpose |
|------|---------|
| START_HERE.md | Entry point |
| DEPLOYMENT_CHECKLIST.md | Step-by-step |
| FULL_DEPLOYMENT.md | Complete guide |
| frontend/QUICK-DEPLOY.md | 60-second |
| frontend/VERCEL.md | Vercel setup |
| frontend/render.md | Render setup |
| backend/DOCKER.md | Docker guide |

## 🌐 Platform URLs

- Render: https://dashboard.render.com
- Vercel: https://vercel.com/dashboard
- GitHub: https://github.com

## 🚀 3-Step Deployment

### Backend (Render)
```
1. Create Web Service
2. Connect GitHub → backend folder
3. Set env vars: DB_URL, JWT_SECRET, etc.
4. Deploy
5. Save URL: https://ledgera-backend.onrender.com
```

### Frontend (Vercel)
```
1. Add Project
2. Connect GitHub → frontend folder
3. Deploy
4. Set VITE_API_URL = backend URL
5. Redeploy
6. Open https://your-app.vercel.app
```

### Test
```
1. Open frontend in browser
2. Check console (F12) - no errors
3. Test login or navigation
4. Check Network tab - API calls working
5. Done! 🎉
```

## ✅ Success Checklist

- [ ] Backend deployed to Render
- [ ] Frontend deployed to Vercel
- [ ] Frontend loads without errors
- [ ] API calls reach backend
- [ ] Login works (if test user exists)
- [ ] Dashboard displays data
- [ ] No CORS errors
- [ ] Page loads < 3 seconds

## 🔧 Environment Variables

**Backend (Render env vars):**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://...
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=...
JWT_SECRET=... (256+ bits)
```

**Frontend (Vercel env vars):**
```
VITE_API_URL=https://your-backend.onrender.com
```

## 💡 Quick Commands

### Local Testing
```bash
cd backend
docker-compose up -d          # Start backend + DB
curl http://localhost:8080/api/health

cd ../frontend
npm run build && npm run preview
# Open http://localhost:4173
```

### Build & Check
```bash
cd backend
docker build -t ledgera-backend .
docker images | grep ledgera

cd ../frontend
npm install && npm run build
ls -lh dist/
```

## 📊 Recommended Setup

| Component | Platform | Cost |
|-----------|----------|------|
| Backend | Render | Free |
| Frontend | Vercel | Free |
| Database | PostgreSQL | Free (Render) |
| **Total** | **Combined** | **$0/month** |

## 🔗 Key Guides by Platform

**Vercel Frontend:**
→ frontend/VERCEL.md (300+ lines)

**Render Backend:**
→ backend/DOCKER.md (500+ lines)

**Integration:**
→ FULL_DEPLOYMENT.md (400+ lines)

## 🆘 Quick Help

**Blank page?** 
→ Check console (F12) for errors

**API calls failing?**
→ Verify VITE_API_URL in Vercel env vars

**CORS error?**
→ Backend needs CORS header for frontend URL

**Build error?**
→ Run locally to debug: npm run build

**Docker error?**
→ Check logs: docker logs ledgera-backend

## 📞 Where to Find Help

- Quick questions → QUICK-DEPLOY.md
- Technical questions → FULL_DEPLOYMENT.md
- Vercel help → frontend/VERCEL.md
- Render help → frontend/render.md
- Docker help → backend/DOCKER.md

## ⏱️ Timeline

- Reading docs: 30 min
- Backend deploy: 30-45 min
- Frontend deploy: 20-30 min
- Testing: 15 min
- **Total: ~1.5 hours**

## 🎉 Success!

When you see:
- ✅ Frontend URL loads
- ✅ No console errors
- ✅ API calls working
- ✅ Features operational

**You're done!** 🚀

---

For complete details, see START_HERE.md

