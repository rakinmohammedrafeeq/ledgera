<p align="center">
  <img src="public/icon.svg" alt="Ledgera Logo" width="170">
</p>

<div align="center">
  
  [![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
  [![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
  [![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue.svg)](https://www.typescriptlang.org/)
  
</div>

# Ledgera – Full-Stack Finance Tracking System

Ledgera is a full-stack collaborative finance platform built for modern teams and personal workflows. It combines workspace-based financial management, shared records, analytics dashboards, and role-based collaboration into a streamlined premium SaaS experience.

---

## Features

### Authentication & Security
- JWT-based authentication and authorization
- OTP-based password reset flow with email integration (Resend API)
- Rate limiting for password reset requests (3 per 15 minutes)
- Multi-level role-based access control (Admin/Analyst/Viewer)
- Secure token management with configurable expiry
- Protected routes and API endpoints

### Workspace Management
- Multi-workspace support for team collaboration
- Workspace-scoped financial records and analytics
- Three permission levels: Owner, Editor, Viewer
- Workspace member management and invitations
- Automatic workspace switching
- Workspace deletion with safety validations

### Financial Management
- Income and expense tracking with custom categories
- Advanced filtering and search capabilities
- Workspace-scoped transaction management
- Permission-based record creation/editing
- Transaction history with user attribution
- Real-time data synchronization

### Analytics & Visualization
- Interactive dashboard with real-time analytics
- Monthly trend charts (income vs expenses)
- Category-wise spending breakdown
- Income vs expense comparisons
- Recent activity feed
- Workspace-specific analytics

### Admin Platform Management
- Dedicated admin panel for platform-wide user management
- User activation/deactivation controls
- Search and filter users by status, role, email
- View user workspace associations
- Prevent self-deactivation safeguards
- Professional admin UX with confirmation modals

### UI/UX Features
- System theme detection (Light/Dark/System)
- Responsive design for mobile and desktop
- Modern glassmorphic UI components
- Smooth animations and transitions
- Toast notifications for user feedback
- Accessible components (WCAG considerations)

### Architecture
- RESTful API with Spring Boot
- Layered architecture (Controller → Service → Repository)
- Database migrations with Flyway
- Comprehensive error handling with user-friendly messages
- Detailed logging and monitoring
- Workspace context management  

---

## Why Ledgera?

Ledgera demonstrates production-grade full-stack development practices:

- **Enterprise Architecture** — Layered backend design with clear separation of concerns
- **Multi-Tenancy** — Workspace-based architecture for team collaboration
- **Security First** — JWT authentication, role-based access control, workspace permissions, rate limiting
- **Modern Stack** — Spring Boot 3, React 18, TypeScript, PostgreSQL
- **Email Integration** — Professional OTP-based password reset flow with Resend API
- **Scalable Design** — RESTful API, database migrations, comprehensive error handling
- **Admin Platform** — Dedicated admin panel for platform-wide user management
- **Modern UX** — System theme detection, responsive design, accessible components
- **Developer Experience** — Hot reload, TypeScript, ESLint, detailed logging

Built to reflect production-level design practices used in modern SaaS applications.

---

## Tech Stack

### Backend
- **Language:** Java 17+
- **Framework:** Spring Boot 3.2.x
- **Security:** Spring Security with JWT
- **Database:** Spring Data JPA, Flyway migrations
- **Email:** Resend API (v3.0.0)
- **Rate Limiting:** Bucket4j (v8.7.0)
- **Build Tool:** Maven

### Frontend
- **Framework:** React 18 with TypeScript
- **Build Tool:** Vite 5
- **Routing:** React Router v6
- **HTTP Client:** Axios
- **Charts:** Recharts
- **UI Components:** Radix UI, Tailwind CSS
- **State Management:** Zustand
- **Forms:** React Hook Form with Zod validation
- **Notifications:** Sonner

### Database
- **Production:** PostgreSQL (Neon serverless)
- **Development:** H2 (in-memory, optional profile)

### DevOps
- **Backend Hosting:** Render (Docker)
- **Frontend Hosting:** Vercel
- **Email Service:** Resend
- **Version Control:** Git

## Repository Structure

```text
ledgera/
├─ backend/                           # Spring Boot API
│  ├─ src/main/java/com/ledgera/
│  │  ├─ config/                      # Configuration classes
│  │  │  ├─ DataInitializer.java     # Seed data
│  │  │  ├─ EmailConfig.java         # Resend email client
│  │  │  ├─ RateLimitConfig.java     # Rate limiting
│  │  │  └─ SecurityConfig.java      # Spring Security
│  │  ├─ controller/                  # REST controllers
│  │  │  ├─ AdminUserController.java
│  │  │  ├─ AuthController.java
│  │  │  ├─ DashboardController.java
│  │  │  ├─ FinancialRecordController.java
│  │  │  ├─ HealthController.java
│  │  │  ├─ OtpController.java
│  │  │  ├─ UserController.java
│  │  │  ├─ WorkspaceController.java
│  │  │  └─ WorkspaceMemberController.java
│  │  ├─ dto/                         # Data Transfer Objects
│  │  ├─ entity/                      # JPA entities
│  │  │  ├─ FinancialRecord.java
│  │  │  ├─ User.java
│  │  │  ├─ Workspace.java
│  │  │  ├─ WorkspaceInvitation.java
│  │  │  └─ WorkspaceMember.java
│  │  ├─ enums/                       # Enumerations
│  │  │  ├─ Role.java
│  │  │  ├─ TransactionType.java
│  │  │  └─ WorkspacePermission.java
│  │  ├─ exception/                   # Exception handling
│  │  │  └─ GlobalExceptionHandler.java
│  │  ├─ repository/                  # Data access layer
│  │  │  ├─ FinancialRecordRepository.java
│  │  │  ├─ FinancialRecordSpecification.java
│  │  │  ├─ UserRepository.java
│  │  │  ├─ WorkspaceRepository.java
│  │  │  ├─ WorkspaceInvitationRepository.java
│  │  │  └─ WorkspaceMemberRepository.java
│  │  ├─ security/                    # Security components
│  │  │  ├─ CustomUserDetailsService.java
│  │  │  ├─ JwtAuthenticationFilter.java
│  │  │  ├─ JwtTokenProvider.java
│  │  │  ├─ RequireWorkspacePermission.java
│  │  │  ├─ WorkspaceContextHolder.java
│  │  │  └─ WorkspacePermissionEvaluator.java
│  │  └─ service/                     # Business logic
│  │     ├─ AdminUserService.java
│  │     ├─ AuthService.java
│  │     ├─ CurrentUserService.java
│  │     ├─ DashboardService.java
│  │     ├─ EmailService.java
│  │     ├─ FinancialRecordService.java
│  │     ├─ UserService.java
│  │     ├─ WorkspaceService.java
│  │     └─ WorkspaceMemberService.java
│  ├─ src/main/resources/
│  │  ├─ application.properties       # Main config
│  │  ├─ application-h2.properties    # H2 profile
│  │  └─ db/migration/                # Flyway migrations
│  │     ├─ V1__init.sql
│  │     ├─ V2__backfill_financial_record_users.sql
│  │     ├─ V3__add_workspaces.sql
│  │     ├─ V4__add_otp_fields.sql
│  │     ├─ V5__update_workspace_names_to_first_name.sql
│  │     └─ V6__ensure_workspace_owners_are_members.sql
│  ├─ .env                            # Environment variables
│  ├─ .env.example                    # Environment template
│  ├─ Dockerfile                      # Docker configuration
│  ├─ docker-compose.yml              # Docker Compose
│  └─ pom.xml                         # Maven dependencies
│
├─ frontend/                          # React + Vite SPA
│  ├─ src/
│  │  ├─ api/                         # API client
│  │  │  ├─ adminApi.ts
│  │  │  ├─ authApi.ts
│  │  │  ├─ client.ts
│  │  │  ├─ dashboardApi.ts
│  │  │  ├─ recordsApi.ts
│  │  │  ├─ usersApi.ts
│  │  │  ├─ workspaceApi.ts
│  │  │  └─ workspaceMemberApi.ts
│  │  ├─ components/                  # React components
│  │  │  ├─ auth/                     # Auth components
│  │  │  ├─ backend/                  # Backend status
│  │  │  ├─ dashboard/                # Dashboard widgets
│  │  │  ├─ landing/                  # Landing page
│  │  │  ├─ layout/                   # Layout components
│  │  │  ├─ records/                  # Record components
│  │  │  ├─ workspace/                # Workspace components
│  │  │  └─ ui/                       # UI primitives
│  │  ├─ config/                      # Configuration
│  │  │  └─ brandAssets.ts            # Logo & branding
│  │  ├─ contexts/                    # React contexts
│  │  │  ├─ AuthContext.tsx
│  │  │  ├─ SidebarContext.tsx
│  │  │  ├─ ThemeContext.tsx
│  │  │  └─ WorkspaceContext.tsx
│  │  ├─ hooks/                       # Custom hooks
│  │  ├─ pages/                       # Page components
│  │  │  ├─ admin/                    # Admin pages
│  │  │  ├─ auth/                     # Auth pages
│  │  │  ├─ dashboard/                # Dashboard page
│  │  │  ├─ records/                  # Records page
│  │  │  ├─ workspace/                # Workspace pages
│  │  │  └─ LandingPage.tsx
│  │  ├─ store/                       # State management
│  │  ├─ types/                       # TypeScript types
│  │  ├─ utils/                       # Utility functions
│  │  ├─ App.tsx                      # Root component
│  │  └─ main.tsx                     # Entry point
│  ├─ public/
│  │  ├─ icon.svg                     # App logo (SVG)
│  │  ├─ icon.png                     # App logo (PNG)
│  │  └─ site.webmanifest             # PWA manifest
│  ├─ .env                            # Environment variables
│  ├─ .env.example                    # Environment template
│  ├─ index.html                      # HTML template
│  ├─ package.json                    # Dependencies
│  ├─ tsconfig.json                   # TypeScript config
│  ├─ vite.config.ts                  # Vite config
│  └─ vercel.json                     # Vercel config
│
├─ public/                            # Shared assets
│  ├─ icon.svg                        # Ledgera logo
│  ├─ icon.png                        # Ledgera logo (PNG)
│  └─ site.webmanifest                # PWA manifest
│
├─ .gitignore                         # Git ignore rules
└─ README.md                          # This file
```

## Environment Configuration

### Backend Environment Variables

Create a `backend/.env` file with the following variables:

```env
# Database Configuration
DB_URL=jdbc:postgresql://your-db-host/your-database?sslmode=require
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-secret-key-at-least-256-bits-long
JWT_EXPIRATION=86400000

# Resend Email Configuration
RESEND_API_KEY=your_resend_api_key
RESEND_FROM_EMAIL=your-verified-email@yourdomain.com
RESEND_FROM_NAME=Ledgera

# Application Configuration
APP_BASE_URL=http://localhost:5173
```

**Note:** Use `backend/.env.example` as a reference template.

### Frontend Environment Variables

Create a `frontend/.env` file:

```env
VITE_API_URL=http://localhost:8080
```

For production deployments:
- Set `VITE_API_URL` to your backend URL (e.g., `https://your-backend.onrender.com`)
- Configure environment variables in your hosting platform (Vercel/Netlify)

## Local Development

### Prerequisites

- **Java 17+** (JDK)
- **Node.js 18+** (frontend includes `.nvmrc` with `18`)
- **npm** or **yarn**
- **PostgreSQL** (or use H2 for testing)
- **Maven** (included via Maven Wrapper)

### Clone the Repository

```bash
git clone https://github.com/yourusername/ledgera.git
cd ledgera
```

### Setup Backend

```bash
cd backend

# Copy environment template
cp .env.example .env

# Edit .env with your configuration
# Add database credentials, JWT secret, Resend API key, etc.

# Run the application
./mvnw spring-boot:run
```

Backend runs on: **http://localhost:8080**

**Optional:** Use H2 in-memory database for testing:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

### Setup Frontend

```bash
cd frontend

# Install dependencies
npm install

# Copy environment template (if needed)
cp .env.example .env

# Start development server
npm run dev
```

Frontend runs on: **http://localhost:5173**

### Access the Application

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080
- **Health Check:** http://localhost:8080/healthz

### Default Admin Credentials

If data initialization is enabled:
- **Email:** rakinmohammedrafeeq@gmail.com
- **Password:** admin123

**Note:** On first login, a default workspace is automatically created for each user.

---

## Build Commands

### Backend Production Build

```bash
cd backend
./mvnw clean package

# Run the JAR
java -jar target/ledgera-*.jar
```

### Frontend Production Build

```bash
cd frontend
npm run build

# Preview production build
npm run preview
```

---

## Available Scripts

### Backend

- `./mvnw spring-boot:run` — Start development server
- `./mvnw clean package` — Build production JAR
- `./mvnw test` — Run tests
- `./mvnw clean` — Clean build artifacts

### Frontend

- `npm run dev` — Start Vite dev server (http://localhost:5173)
- `npm run build` — Production build
- `npm run preview` — Preview production build
- `npm run lint` — Run ESLint
- `npm run type-check` — TypeScript type checking

## API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` — User registration
- `POST /api/auth/login` — User login (returns JWT)
- `POST /api/auth/request-otp` — Request OTP for password reset
- `POST /api/auth/verify-otp` — Verify OTP code
- `POST /api/auth/reset-password` — Reset password with OTP

### Users (`/api/users`)
- `GET /api/users/me` — Get current user profile
- `PUT /api/users/me` — Update current user profile

### Admin Users (`/api/admin/users`)
- `GET /api/admin/users` — List all users with pagination (Admin only)
- `PUT /api/admin/users/{id}/status` — Activate/deactivate user (Admin only)

### Workspaces (`/api/workspaces`)
- `GET /api/workspaces` — List user's workspaces
- `POST /api/workspaces` — Create new workspace
- `GET /api/workspaces/{id}` — Get workspace details
- `PUT /api/workspaces/{id}` — Update workspace (Owner only)
- `DELETE /api/workspaces/{id}` — Delete workspace (Owner only)
- `POST /api/workspaces/{id}/switch` — Switch to workspace

### Workspace Members (`/api/workspaces/{workspaceId}/members`)
- `GET /api/workspaces/{workspaceId}/members` — List workspace members
- `POST /api/workspaces/{workspaceId}/members/invite` — Invite member (Owner only)
- `PUT /api/workspaces/{workspaceId}/members/{memberId}` — Update member permission (Owner only)
- `DELETE /api/workspaces/{workspaceId}/members/{memberId}` — Remove member (Owner only)

### Financial Records (`/api/records`)
- `GET /api/records` — List records with filtering (workspace-scoped)
- `POST /api/records` — Create new record (Editor/Owner only)
- `GET /api/records/{id}` — Get record by ID
- `PUT /api/records/{id}` — Update record (Editor/Owner only)
- `DELETE /api/records/{id}` — Delete record (Editor/Owner only)

### Dashboard (`/api/dashboard`)
- `GET /api/dashboard` — Get dashboard analytics (workspace-scoped)

### Health Check
- `GET /healthz` — Health check endpoint (unauthenticated)

## Deployment

### Backend Deployment (Render)

1. **Create a new Web Service** on Render
2. **Connect your repository**
3. **Configure build settings:**
   - Build Command: `cd backend && ./mvnw clean package`
   - Start Command: `java -jar backend/target/*.jar`
4. **Set environment variables:**
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
   - `JWT_SECRET`, `JWT_EXPIRATION`
   - `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `RESEND_FROM_NAME`
   - `APP_BASE_URL` (your frontend URL)
5. **Health check:** `/healthz`

### Frontend Deployment (Vercel)

1. **Import your repository** to Vercel
2. **Configure build settings:**
   - Framework Preset: Vite
   - Root Directory: `frontend`
   - Build Command: `npm run build`
   - Output Directory: `dist`
3. **Set environment variables:**
   - `VITE_API_URL` (your backend URL)
4. **Deploy**

### Email Configuration (Resend)

1. **Sign up** at [resend.com](https://resend.com)
2. **Verify your domain** or use `onboarding@resend.dev` for testing
3. **Generate API key** and add to backend environment variables
4. **Configure email templates** in `EmailService.java`

### Database Setup (Neon)

1. **Create a PostgreSQL database** on [Neon](https://neon.tech)
2. **Copy connection string** to `DB_URL`
3. **Flyway migrations** run automatically on startup

## Screenshots

### Landing Page
Interactive showcase with animated statistics and smooth transitions.

### Dashboard
Real-time analytics with monthly trends, category breakdowns, and recent activity. Workspace-scoped data visualization.

### Financial Records
Advanced filtering, search, and management of transactions. Permission-based access controls.

### Workspace Management
Create and manage multiple workspaces. Invite team members with granular permissions (Owner/Editor/Viewer).

### Admin Panel
Platform-wide user management with activation controls, search, and filtering capabilities.

### Authentication
Secure login, registration, and OTP-based password reset flow.

### Theme System
Automatic system theme detection with manual Light/Dark/System mode selection.

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact  

For questions, suggestions, or collaboration:

- **Email:** rakinmohammedrafeeq@gmail.com  
- **LinkedIn:** [linkedin.com/in/rakinmohammedrafeeq](https://www.linkedin.com/in/rakinmohammedrafeeq)  
- **GitHub:** [github.com/rakinmohammedrafeeq](https://github.com/rakinmohammedrafeeq)

---

## Support  

If you find this project useful:

- ⭐ Star the repository on GitHub
- Report issues or suggest features
- Contribute via pull requests
- ☕ Support my work:

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://buymeacoffee.com/rakinmohammedrafeeq)

---

<div align="center">
  <img src="public/icon.svg" alt="Ledgera Logo" width="60" height="60">
  
  Built with ❤️ by [Rakin Mohammed Rafeeq](https://github.com/rakinmohammedrafeeq)
</div>
