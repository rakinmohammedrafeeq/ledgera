# Logo & Favicon Setup - Complete Implementation

## Overview
This document describes the complete logo and favicon setup for the Ledgera application, ensuring consistent branding across all pages and browsers.

## Files Created/Modified

### 1. **public/icon.svg** (NEW)
- Modern SVG logo with gradient background
- Features a ledger book icon with currency symbol
- Scalable vector format for crisp rendering at any size
- Primary brand colors: #6366f1 (indigo) and #8b5cf6 (purple)

### 2. **public/icon.png** (EXISTING)
- 512x512 PNG fallback for browsers that don't support SVG
- Used for Apple touch icons and PWA icons

### 3. **public/site.webmanifest** (NEW)
- Progressive Web App manifest
- Defines app name, icons, theme colors, and display mode
- Enables "Add to Home Screen" functionality

### 4. **frontend/index.html** (MODIFIED)
Updated favicon references:
```html
<!-- Favicons -->
<link rel="icon" type="image/svg+xml" href="/icon.svg" />
<link rel="icon" type="image/png" sizes="32x32" href="/icon.png" />
<link rel="apple-touch-icon" sizes="180x180" href="/icon.png" />
<link rel="manifest" href="/site.webmanifest" />
```

Added theme color support for light/dark modes:
```html
<meta name="theme-color" content="#6366f1" media="(prefers-color-scheme: light)" />
<meta name="theme-color" content="#0f0f14" media="(prefers-color-scheme: dark)" />
```

### 5. **public/icon.html** (DELETED)
- Removed old HTML file with embedded base64 image

## Logo Usage Throughout Application

The logo is consistently used via the centralized configuration:

### Configuration File
**frontend/src/config/brandAssets.ts**
```typescript
export const APP_LOGO_SRC = '/icon.svg'
export const APP_LOGO_PNG_SRC = '/icon.png'
```

### Components Using Logo

1. **Sidebar** (`frontend/src/components/layout/AppSidebar.tsx`)
   - Logo in top-left corner (32x32px)
   - Displays on all authenticated pages

2. **Navbar** (`frontend/src/components/layout/Navbar.tsx`)
   - No direct logo (uses user avatar)

3. **Landing Page** (`frontend/src/pages/LandingPage.tsx`)
   - Logo in header (36x36px)
   - Visible to all visitors

4. **Login Page** (`frontend/src/pages/auth/LoginPage.tsx`)
   - Centered logo (56x56px)
   - First impression for users

5. **Register Page** (`frontend/src/pages/auth/RegisterPage.tsx`)
   - Centered logo (48x48px)
   - Consistent with login page

6. **Auth Layout** (`frontend/src/components/layout/AuthLayout.tsx`)
   - Logo in left panel (40x40px)
   - Shown on all auth pages

7. **Footer** (`frontend/src/components/layout/AppFooter.tsx`)
   - Logo with app name (28x28px)
   - Clickable to return home

8. **Loading Spinner** (`frontend/src/components/ui/fintrix-spinner.tsx`)
   - Animated spinner with logo in center
   - Used during login, register, and password reset
   - Provides branded loading experience

## Browser Compatibility

### Modern Browsers
- Chrome, Firefox, Safari, Edge: Use SVG favicon
- Crisp rendering at any size
- Supports theme colors

### Legacy Browsers
- Fallback to PNG favicon
- Still provides good quality at standard sizes

### Mobile Devices
- iOS: Uses Apple touch icon (180x180)
- Android: Uses PWA manifest icons
- Windows: Uses standard favicon

## Theme Support

### Light Mode
- Theme color: #6366f1 (primary indigo)
- Matches brand gradient

### Dark Mode
- Theme color: #0f0f14 (dark background)
- Consistent with app theme

## PWA Features

The site.webmanifest enables:
- Add to Home Screen on mobile
- Standalone app mode
- Custom splash screen
- App name and description

## Testing Checklist

✅ Browser tab favicon displays correctly
✅ Logo appears in sidebar
✅ Logo appears on landing page
✅ Logo appears on login page
✅ Logo appears on register page
✅ Logo appears in auth layout
✅ Logo appears in footer
✅ Loading spinner shows logo
✅ Apple touch icon works on iOS
✅ PWA manifest is valid
✅ Theme colors apply correctly
✅ SVG scales properly at all sizes
✅ PNG fallback works in legacy browsers

## Maintenance

### Updating the Logo
1. Replace `public/icon.svg` with new SVG
2. Replace `public/icon.png` with new PNG (512x512)
3. Rebuild the application: `npm run build`
4. No code changes needed - all components use centralized config

### Adding New Sizes
1. Generate new size from icon.png
2. Add reference in index.html
3. Update site.webmanifest if needed

## File Locations Summary

```
ledgera/
├── public/
│   ├── icon.svg          # Primary logo (SVG)
│   ├── icon.png          # Fallback logo (512x512 PNG)
│   └── site.webmanifest  # PWA manifest
├── frontend/
│   ├── index.html        # Favicon references
│   └── src/
│       ├── config/
│       │   └── brandAssets.ts  # Logo path constants
│       ├── components/
│       │   ├── layout/
│       │   │   ├── AppSidebar.tsx
│       │   │   ├── AppFooter.tsx
│       │   │   └── AuthLayout.tsx
│       │   └── ui/
│       │       └── fintrix-spinner.tsx
│       └── pages/
│           ├── LandingPage.tsx
│           └── auth/
│               ├── LoginPage.tsx
│               └── RegisterPage.tsx
```

## Notes

- All logo references use the centralized `APP_LOGO_SRC` constant
- No hardcoded paths in components
- Easy to update branding in one place
- Consistent sizing and styling across the app
- Optimized for performance with eager loading on critical pages
- Lazy loading on less critical pages (footer)
