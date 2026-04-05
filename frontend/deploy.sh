#!/bin/bash

# Ledgera Frontend Deployment Script
# Automated build and deployment to Vercel or Render

set -e

echo "🚀 Ledgera Frontend Deployment Script"
echo "======================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if we're in the frontend directory
if [ ! -f "package.json" ]; then
    echo "❌ Error: package.json not found. Are you in the frontend directory?"
    exit 1
fi

echo -e "${BLUE}1. Installing dependencies...${NC}"
npm install

echo ""
echo -e "${BLUE}2. Running build...${NC}"
npm run build

echo ""
echo -e "${BLUE}3. Verifying build...${NC}"
if [ -d "dist" ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo -e "${GREEN}✓ Output directory: dist/$(NC}"
    echo -e "${GREEN}✓ Files: $(ls dist/ | wc -l) files${NC}"
else
    echo "❌ Error: Build failed. dist/ directory not found."
    exit 1
fi

echo ""
echo -e "${YELLOW}Deployment Options:${NC}"
echo "1. Deploy to Vercel:  vercel deploy --prod"
echo "2. Deploy to Render:  Automatically on git push to main"
echo ""
echo -e "${GREEN}✓ Frontend is ready for deployment!${NC}"
echo ""
echo "Next steps:"
echo "1. Ensure backend is deployed and accessible"
echo "2. Set VITE_API_URL in platform dashboard"
echo "3. Push to GitHub for automatic deployment"
echo ""

