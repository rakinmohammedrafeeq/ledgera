# 🐳 Ledgera Backend - Docker & Deployment Guide

## 📋 Overview

This directory contains production-ready Docker configurations for the Ledgera Finance Backend (Spring Boot 3.2.5, Java 17, Maven).

### What's Included

✅ **Dockerfile** - Multi-stage production build optimized for size
✅ **Dockerfile.prod** - Ultra-lean Alpine variant for cloud platforms
✅ **.dockerignore** - Build exclusions to minimize image size
✅ **docker-compose.yml** - Local development (Backend + PostgreSQL)
✅ **DOCKER.md** - Comprehensive reference guide (50+ examples)
✅ **DOCKER-QUICKSTART.md** - Quick reference for common tasks
✅ **Makefile** - Convenient commands for Docker operations
✅ **GitHub Actions** - Automated image builds and registry pushes

---

## 🚀 Quick Start

### 1. Build the Image

```bash
cd backend
docker build -t ledgera-backend:latest .
```

### 2. Run with External Database

```bash
docker run -d \
  --name ledgera-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://your-db-host:5432/ledgera_db" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="your-password" \
  ledgera-backend:latest
```

### 3. Or Run with Docker Compose (Includes PostgreSQL)

```bash
cd backend
docker-compose up -d
# Backend runs on http://localhost:8080
# Database available at localhost:5432
```

---

## 📚 Documentation Files

| File | Purpose | Use When |
|------|---------|----------|
| **DOCKER.md** | Complete reference with examples | Learning Docker setup, deployment details |
| **DOCKER-QUICKSTART.md** | Focused quick reference | Need quick command reminders |
| **Makefile** | Command shortcuts | Using `make` for operations |
| **README.md** (this file) | Overview | Understanding the setup |

---

## 🏗️ Architecture

### Multi-Stage Build Strategy

```
Stage 1: Builder (Temporary)
├─ Maven 3.9
├─ JDK 17
├─ Downloads dependencies
└─ Compiles to JAR

     ↓ (Copy JAR only)

Stage 2: Runtime (Final Image)
├─ JRE 17 (lightweight)
├─ Non-root user
├─ Health checks
└─ ~400-500MB final size
```

**Benefits:**
- Reduces final image by 50%+ (no build tools in production)
- Faster deployments (smaller artifacts)
- Improved security (minimal attack surface)
- Better caching (separate dependency layer)

---

## 🔧 Configuration

All configuration uses environment variables (no hardcoded values):

### Required Variables

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/ledgera_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=Ledgera2024SecretKeyForJWTAuthenticationHS256...
```

### Optional Variables

```env
PORT=8080                          # Default: 8080 (for cloud platforms)
JWT_EXPIRATION=86400000           # Default: 86400000 (24 hours)
JAVA_OPTS=-Xmx256m               # JVM memory settings
```

---

## 🌐 Cloud Deployment

### Render.com (Recommended)

```bash
# 1. Connect GitHub repo in Render dashboard
# 2. Set environment variables:
PORT=10000  # Render auto-detects and assigns
SPRING_DATASOURCE_URL=postgresql://...
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=pass
JWT_SECRET=...

# 3. Render auto-deploys on push to main
```

### Heroku

```bash
heroku login
heroku container:login
docker tag ledgera-backend:latest registry.heroku.com/your-app/web
docker push registry.heroku.com/your-app/web
heroku container:release web --app your-app
```

### AWS (ECR + ECS)

```bash
aws ecr get-login-password | docker login --username AWS --password-stdin <id>.dkr.ecr.<region>.amazonaws.com
docker tag ledgera-backend <id>.dkr.ecr.<region>.amazonaws.com/ledgera:latest
docker push <id>.dkr.ecr.<region>.amazonaws.com/ledgera:latest
```

---

## 📊 Common Tasks

### Build

```bash
# Standard build
docker build -t ledgera-backend:latest .

# Production build (Alpine, smaller)
docker build -f Dockerfile.prod -t ledgera-backend:prod .

# With version tag
docker build -t ledgera-backend:1.0.0 .
```

### Run

```bash
# Basic run (requires external DB)
docker run -d -p 8080:8080 ledgera-backend:latest

# With environment variables
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="..." \
  -e SPRING_DATASOURCE_USERNAME="..." \
  ledgera-backend:latest

# Custom port
docker run -d -p 3000:8080 \
  -e PORT=8080 \
  ledgera-backend:latest
```

### Monitor

```bash
# List containers
docker ps | grep ledgera

# View logs
docker logs -f ledgera-backend

# Resource usage
docker stats ledgera-backend

# Health status
docker inspect --format='{{.State.Health.Status}}' ledgera-backend
```

### Manage

```bash
# Stop container
docker stop ledgera-backend

# Remove container
docker rm ledgera-backend

# Remove image
docker rmi ledgera-backend:latest

# Full cleanup
docker system prune -a
```

---

## 🛡️ Security Features

✅ **Non-root user** - Runs as `appuser:1001`
✅ **Minimal image** - No build tools, no shell (Alpine variant)
✅ **Health checks** - Automatic restart on failure
✅ **Environment variables** - No hardcoded secrets
✅ **JVM hardening** - G1GC tuned for containers
✅ **Read-only option** - Can run with `--read-only` flag

### Running with Security Restrictions

```bash
docker run -d \
  --user appuser \
  --read-only \
  --tmpfs /tmp \
  --memory 512m \
  --cpus 1 \
  -p 8080:8080 \
  ledgera-backend:latest
```

---

## 🐳 Using Make

If you have `make` installed, use these shortcuts:

```bash
make help                 # Show all commands
make docker-build         # Build standard image
make docker-build-prod    # Build Alpine variant
make docker-run           # Run container
make docker-compose-up    # Start with PostgreSQL
make docker-compose-down  # Stop all services
make docker-logs          # View container logs
make docker-shell         # Open shell in container
make docker-clean         # Remove container & image
```

---

## 🔍 Troubleshooting

### Container won't start
```bash
docker logs ledgera-backend
# Check database connectivity
# Verify environment variables are set
```

### Port already in use
```bash
# Use different port
docker run -p 9000:8080 ledgera-backend:latest

# Or find/kill process
lsof -i :8080
```

### Database connection failed
```bash
# Verify environment variables
docker exec ledgera-backend env | grep SPRING_DATASOURCE

# Test connectivity from container
docker exec ledgera-backend ping database-host
```

### Out of memory
```bash
docker run -e JAVA_OPTS="-Xmx256m -Xms128m" ledgera-backend:latest
```

See **DOCKER.md** for more troubleshooting scenarios.

---

## 📦 Image Sizes

| Variant | Base | Size | Use Case |
|---------|------|------|----------|
| **Standard** | `eclipse-temurin:17-jre` | ~450MB | General production |
| **Alpine** | `eclipse-temurin:17-jre-alpine` | ~350MB | Memory-constrained, lean deployments |

---

## ✅ Verification Checklist

After setup, verify:

- [ ] Build succeeds: `docker build -t ledgera-backend .`
- [ ] Image exists: `docker images | grep ledgera`
- [ ] Container runs: `docker run -p 8080:8080 ledgera-backend:latest`
- [ ] API responds: `curl http://localhost:8080/api/records`
- [ ] Health check passes: `docker inspect ledgera-backend --format='{{.State.Health.Status}}'`
- [ ] Compose works: `docker-compose up -d && docker-compose ps`

---

## 📁 File Structure

```
backend/
├── Dockerfile                  # Main production Dockerfile
├── Dockerfile.prod             # Alpine-optimized variant
├── .dockerignore               # Build exclusions
├── docker-compose.yml          # Local dev environment
├── DOCKER.md                   # Complete reference (50+ examples)
├── DOCKER-QUICKSTART.md        # Quick reference
├── README.md                   # This file
├── Makefile                    # Command shortcuts
├── pom.xml                     # Maven config
└── src/                        # Source code
```

---

## 🔄 CI/CD Integration

### GitHub Actions

Auto-build and push on every commit to `main`:

```bash
.github/workflows/docker-build.yml
```

Pushes to GitHub Container Registry (GHCR). Configure with:
- Repository secrets (optional)
- Push to Docker Hub or ECR by modifying workflow

### Manual Build & Push

```bash
# Build
docker build -t username/ledgera-backend:latest .

# Push to Docker Hub
docker push username/ledgera-backend:latest
```

---

## 🚢 Deployment Checklist

Before deploying to production:

- [ ] All environment variables documented
- [ ] Database migration strategy confirmed (Flyway)
- [ ] Image scanned for vulnerabilities (optional: `trivy image`)
- [ ] Resource limits configured (memory, CPU)
- [ ] Logs aggregation set up (if needed)
- [ ] Health check endpoint working
- [ ] Secrets management configured (no hardcoded values)

---

## 📖 Additional Resources

- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Docker Documentation](https://docs.docker.com/)
- [Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Render.com Deployment](https://render.com/docs)

---

## 💡 Tips & Best Practices

1. **Always use version tags** in production (not `latest`)
2. **Keep images lean** - use `.dockerignore` religiously
3. **Cache effectively** - put dependencies before source code
4. **Environment-first** - all config from environment variables
5. **Monitor size** - run `docker images` to track bloat
6. **Use health checks** - let platforms auto-restart on failure
7. **Non-root by default** - security best practice
8. **Test locally first** - use docker-compose before pushing

---

## 🆘 Getting Help

- **Docker issues**: Check logs with `docker logs <container>`
- **Build issues**: Enable `docker build --progress=plain` for detailed output
- **Deployment help**: See cloud platform documentation
- **Spring Boot**: Reference [spring.io](https://spring.io)

---

## 📝 Notes

- Java 17 is required (matches pom.xml)
- Maven 3.9 used for build stage
- PostgreSQL recommended for production (H2 for testing)
- CORS configured in application.properties
- JWT authentication enabled by default

---

**Last Updated**: April 2026
**Status**: ✅ Production-Ready
**Tested**: Linux, macOS, Windows (WSL2)

---

For detailed examples and comprehensive guide, see **DOCKER.md**.

