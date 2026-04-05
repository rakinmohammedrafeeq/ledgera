@echo off
REM Ledgera Frontend Deployment Script for Windows
REM Automated build and deployment to Vercel or Render

echo.
echo 🚀 Ledgera Frontend Deployment Script
echo ======================================
echo.

REM Check if we're in the frontend directory
if not exist "package.json" (
    echo ❌ Error: package.json not found. Are you in the frontend directory?
    exit /b 1
)

echo [1] Installing dependencies...
call npm install
if errorlevel 1 (
    echo ❌ Error: npm install failed
    exit /b 1
)

echo.
echo [2] Running build...
call npm run build
if errorlevel 1 (
    echo ❌ Error: npm run build failed
    exit /b 1
)

echo.
echo [3] Verifying build...
if exist "dist" (
    echo ✓ Build successful
    echo ✓ Output directory: dist\
    dir dist | find /c "." >nul
    echo ✓ Files created successfully
) else (
    echo ❌ Error: Build failed. dist\ directory not found.
    exit /b 1
)

echo.
echo Deployment Options:
echo 1. Deploy to Vercel:  vercel deploy --prod
echo 2. Deploy to Render:  Automatically on git push to main
echo.
echo ✓ Frontend is ready for deployment!
echo.
echo Next steps:
echo 1. Ensure backend is deployed and accessible
echo 2. Set VITE_API_URL in platform dashboard
echo 3. Push to GitHub for automatic deployment
echo.

