<p align="center">
  <img src="public/icon.svg" alt="Ledgera Logo" width="170">
</p>

<p align="center">
  <a href="https://ledgera-finance-system.vercel.app"><img src="https://img.shields.io/badge/Demo-Live-success?style=for-the-badge" alt="Live Demo"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge" alt="License"/></a>
  <a href="CHANGELOG.md"><img src="https://img.shields.io/badge/Version-1.2.0-orange.svg?style=for-the-badge" alt="Version"/></a>
  <a href="CONTRIBUTING.md"><img src="https://img.shields.io/badge/Contributions-Welcome-brightgreen.svg?style=for-the-badge" alt="Contributions"/></a>
</p>

<div align="center">
  
  [![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![React](https://img.shields.io/badge/React-18.3-blue.svg)](https://reactjs.org/)
  [![TypeScript](https://img.shields.io/badge/TypeScript-5.7-blue.svg)](https://www.typescriptlang.org/)
  [![Vite](https://img.shields.io/badge/Vite-5.4-646CFF.svg)](https://vite.dev/)
  [![TanStack Query](https://img.shields.io/badge/TanStack%20Query-5.60-FF4154.svg)](https://tanstack.com/query)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791.svg)](https://www.postgresql.org/)
  [![pgvector](https://img.shields.io/badge/pgvector-0.1.4-4169E1.svg)](https://github.com/pgvector/pgvector)
  
</div>

# Ledgera – AI-Powered Full-Stack Finance Tracking Platform

Ledgera is a production-grade, AI-powered collaborative finance platform built for teams and enterprises. It combines cutting-edge AI capabilities (Groq + Gemini 3.6 + RAG with pgvector), multi-workspace collaboration, comprehensive analytics, and enterprise-level security into a modern SaaS application.

**Key Highlights:**
- 🤖 **Advanced AI Architecture** - Groq AI (Llama 3.3 70B), Gemini 3.6 Flash (OCR), AI Agent with tool-calling, RAG-powered financial advisor with local embeddings (all-MiniLM-L6-v2)
- 🧠 **RAG Financial Advisor + AI Agent** - PostgreSQL pgvector semantic search + context-aware investment advice + autonomous agent with 6 tools
- 🔐 **Enterprise Security** - Google OAuth 2.0, JWT authentication, Bucket4j rate limiting, RBAC, admin platform
- 👥 **Multi-Workspace Collaboration** - Team management with granular permissions (Owner/Editor/Viewer)
- 📊 **Real-Time Analytics** - Interactive dashboards with TanStack Query, Recharts visualization, category breakdowns
- ☁️ **Cloud Infrastructure** - Cloudinary CDN, Neon PostgreSQL with pgvector extension, Resend email API
- 🎨 **Modern UX** - Responsive design, glassmorphic UI with Radix UI + Tailwind CSS 4.x, system theme detection

---

## Features

## Features

### 🤖 AI-Powered Features (Advanced Architecture)
- **Smart Transaction Categorization** - AI automatically suggests categories and transaction types with confidence scoring (powered by Groq AI - Llama 3.3 70B Versatile @ 280 tokens/sec)
- **Receipt OCR & Auto-Entry** - Upload receipt photos and extract amount, merchant, date, category, and type automatically (powered by Gemini 3.6 Flash with enhanced accuracy)
- **Cloudinary Cloud Storage** - Enterprise-grade receipt image storage with CDN delivery, automatic optimization, global edge caching, and 25GB free tier
- **AI Financial Insights** - Get personalized spending analysis, budget recommendations, savings rate tracking, and trend analysis (powered by Groq AI with ~0.95s time-to-first-token)
- **AI Agent with Tool-Calling** - Autonomous AI agent loop using Groq Llama 3.3 70B with 6 registered tools:
  - `get_transactions` - Query and filter financial records
  - `get_spending_summary` - Analyze spending by category
  - `search_records` - Full-text search across transactions
  - `get_monthly_trends` - Track income/expense trends over time
  - `get_budget_status` - Calculate budget health and recommendations
  - `create_transaction` - Create new transactions (with user confirmation)
  - Write-confirmation flow with TTL pending action store for secure transaction creation
  - Multi-step reasoning and autonomous tool orchestration
- **RAG Financial Advisor** - Advanced AI advisor using Retrieval-Augmented Generation:
  - Local sentence transformers for embeddings (all-MiniLM-L6-v2, 384 dimensions) via Deep Java Library (DJL) 0.28.0
  - PostgreSQL pgvector 0.1.4 for semantic vector search
  - Context-aware investment advice based on your actual financial records
  - Portfolio recommendations, tax strategies, wealth-building guidance
  - Session-based conversations with memory and semantic retrieval
  - Tabbed UI with AI Advisor + AI Agent on same page
- **Hybrid Provider Strategy** - Optimal quota management using Groq (text), Gemini 3.6 (vision), and local models (embeddings)
- Real-time AI suggestions with sub-second response times (average ~0.5s)
- Multimodal AI processing for text and image analysis
- Automatic model fallback for resilience and quota management

### Authentication & Security
- JWT-based stateless authentication and authorization
- **Google OAuth 2.0 social login** (Continue with Google - seamless integration)
- Dual authentication strategy (Email+Password OR Google Sign-In)
- OTP-based password reset flow with email integration (Resend API v3.0.0)
- Rate limiting with Bucket4j (3 OTP requests per 15 minutes, configurable per-endpoint)
- Multi-level role-based access control (RBAC):
  - Platform roles: Admin, Analyst, Viewer
  - Workspace permissions: Owner, Editor, Viewer
- Method-level security with Spring Security annotations
- Secure token management with configurable expiry (24h default)
- Protected routes and API endpoints
- BCrypt password hashing
- CORS configuration for production and development
- Session management: Stateless (JWT only)

### Workspace Management
- Multi-workspace support for unlimited team collaboration
- Workspace-scoped financial records and analytics with complete data isolation
- Three granular permission levels:
  - **Owner:** Full control, member management, workspace deletion
  - **Editor:** Create/edit/delete records, full data access
  - **Viewer:** Read-only access to all workspace data
- Workspace member management with email invitations
- Permission inheritance (workspace permissions control record access)
- Automatic workspace switching with query invalidation
- Workspace deletion with safety validations
- Member removal and permission updates (Owner only)
- Real-time workspace synchronization

### Financial Management
- Income and expense tracking with custom categories
- Advanced filtering and search capabilities
- Workspace-scoped transaction management
- Permission-based record creation/editing
- Transaction history with user attribution
- Real-time data synchronization

### Analytics & Visualization
- Interactive dashboard with real-time analytics powered by TanStack Query
- Monthly cash flow trends (area charts with gradient fills)
- Category-wise spending breakdown (horizontal bar charts)
- Income vs expense comparisons with period-over-period analysis
- Recent activity feed with user attribution
- Workspace-specific analytics with automatic filtering
- Custom chart tooltips with formatted currency
- Responsive chart design for mobile and desktop
- Export-ready data visualization

### Admin Platform Management
- Dedicated admin panel for platform-wide user administration
- User activation/deactivation controls with confirmation modals
- Advanced search and filtering:
  - Search by name or email
  - Filter by status (active/inactive)
  - Sort by name, email, role, created date
  - Pagination (15 users per page, configurable)
- View user workspace associations and membership details
- Prevent self-deactivation safeguards
- Professional admin UX with:
  - Current user highlighting
  - Confirmation modals for destructive actions
  - Real-time status updates
  - Workspace count display
- Admin account management (promote users, merge accounts)

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

Ledgera demonstrates production-grade full-stack development with cutting-edge AI integration:

- **Hybrid AI Architecture** — Optimal quota management using Groq (Llama 3.3 70B @ 280 tokens/sec) + Gemini 3.6 Flash (enhanced OCR)
- **RAG Financial Advisor** — PostgreSQL pgvector + local embeddings (DJL 0.28.0) for semantic search & context-aware advice
- **Enterprise Architecture** — Layered backend design with clear separation of concerns (Controller → Service → Repository)
- **Multi-Tenancy** — Workspace-based architecture with complete data isolation for seamless team collaboration
- **Security First** — Google OAuth 2.0, JWT authentication (JJWT 0.12.5), RBAC, workspace permissions, Bucket4j rate limiting
- **Modern Stack** — Spring Boot 3.2.5, React 18.3, TypeScript 5.7, PostgreSQL 15+, Vite 5.4, TanStack Query 5.60
- **AI-Powered** — Smart categorization (~0.5s response), receipt OCR (Gemini 3.6), financial insights, RAG advisor
- **Email Integration** — Professional OTP-based password reset flow with Resend API v3.0.0
- **Scalable Design** — RESTful API, Flyway migrations, comprehensive error handling, Docker containerization
- **Admin Platform** — Dedicated admin panel for platform-wide user management with search & filtering
- **Modern UX** — System theme detection, responsive design, accessible components (Radix UI), glassmorphic UI (Tailwind CSS 4.2)
- **Cloud-Native** — Cloudinary CDN (25GB free), serverless PostgreSQL (Neon), Vercel edge deployment
- **Developer Experience** — Hot reload (Vite), TypeScript strict mode, ESLint, detailed logging, API documentation

Built to reflect production-level design practices used in modern AI-powered SaaS applications.

---

## Tech Stack

### Backend
- **Language:** Java 17+
- **Framework:** Spring Boot 3.2.5 (2024 stable release)
- **AI Integration:** 
  - **Groq AI** - Text categorization & insights (Llama 3.3 70B Versatile @ $0.59 input / $0.79 output per 1M tokens)
  - **Gemini 3.6 Flash** - Receipt OCR & image understanding (12% faster than predecessor, superior document processing)
  - **Deep Java Library (DJL) 0.28.0** - Local embeddings (sentence-transformers/all-MiniLM-L6-v2)
- **Vector Database:** PostgreSQL with pgvector 0.1.4 extension
- **Cloud Storage:** Cloudinary 1.38.0 (Image CDN & Storage with 25GB free tier)
- **Authentication:** 
  - JWT (JJWT 0.12.5)
  - Google OAuth 2.0 (Spring OAuth2 Client)
- **Security:** Spring Security 6.x with JWT + OAuth2 + RBAC
- **Database:** Spring Data JPA, Flyway migrations
- **Email:** Resend API v3.0.0 (Transactional email)
- **Rate Limiting:** Bucket4j v8.7.0 (Token bucket algorithm)
- **Build Tool:** Maven with Maven Wrapper
- **HTTP Client:** Apache HttpClient5 v5.3.1

### Frontend
- **Framework:** React 18.3.1 with TypeScript 5.7.3
- **Build Tool:** Vite 5.4.10 (Next-gen frontend tooling)
- **Routing:** React Router v6.28.0 (Client-side routing)
- **HTTP Client:** Axios 1.7.7 (API communication)
- **Data Fetching:** TanStack Query v5.60.0 (Server state management)
- **Charts:** Recharts 3.10.0 (Responsive charting library)
- **UI Components:** Radix UI primitives + Tailwind CSS 4.2.0
- **Forms:** React Hook Form 7.54.1 + Zod 3.24.1 (Type-safe validation)
- **Notifications:** Sonner 1.7.1 (Toast notifications)
- **Icons:** Lucide React 0.564.0 (Modern icon library)

### Database
- **Production:** PostgreSQL 15+ with pgvector extension (Neon serverless)
- **Development:** H2 (in-memory database, optional profile)
- **Migrations:** Flyway (version-controlled schema management)

### DevOps & Infrastructure
- **Backend Hosting:** Render (Docker containerization)
- **Frontend Hosting:** Vercel (Edge network deployment)
- **Email Service:** Resend (99.9% deliverability SLA)
- **CDN:** Cloudinary (Global edge caching)
- **Version Control:** Git + GitHub
- **Container:** Docker with multi-stage builds

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
│  │  │  ├─ AgentController.java      # AI Agent endpoints
│  │  │  ├─ AiController.java         # AI categorization/OCR
│  │  │  ├─ AuthController.java
│  │  │  ├─ DashboardController.java
│  │  │  ├─ FinancialAdvisorController.java  # RAG advisor
│  │  │  ├─ FinancialRecordController.java
│  │  │  ├─ HealthController.java
│  │  │  ├─ OtpController.java
│  │  │  ├─ UserController.java
│  │  │  ├─ WorkspaceController.java
│  │  │  └─ WorkspaceMemberController.java
│  │  ├─ dto/                         # Data Transfer Objects
│  │  │  ├─ AdvisorChatRequest.java
│  │  │  ├─ AdvisorChatResponse.java
│  │  │  ├─ AgentRequest.java
│  │  │  ├─ AgentResponse.java
│  │  │  ├─ AiCategorizationRequest.java
│  │  │  ├─ AiCategorizationResponse.java
│  │  │  ├─ ConfirmActionRequest.java
│  │  │  ├─ PendingAction.java
│  │  │  └─ ... (other DTOs)
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
│  │     ├─ AgentOrchestrationService.java    # Agent loop
│  │     ├─ AgentToolRegistry.java            # Tool registration
│  │     ├─ AgentToolExecutorService.java     # Tool execution
│  │     ├─ PendingActionStore.java           # Confirmation store
│  │     ├─ AuthService.java
│  │     ├─ CurrentUserService.java
│  │     ├─ DashboardService.java
│  │     ├─ EmailService.java
│  │     ├─ EmbeddingService.java             # RAG embeddings
│  │     ├─ FinancialAdvisorService.java      # RAG advisor
│  │     ├─ FinancialRecordService.java
│  │     ├─ GroqAiService.java                # Groq integration
│  │     ├─ GeminiAiService.java              # Gemini integration
│  │     ├─ UserService.java
│  │     ├─ VectorSearchService.java          # Semantic search
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
│  │  │  ├─ advisorApi.ts             # RAG advisor
│  │  │  ├─ agentApi.ts               # AI Agent
│  │  │  ├─ aiApi.ts                  # AI categorization/OCR
│  │  │  ├─ authApi.ts
│  │  │  ├─ client.ts
│  │  │  ├─ dashboardApi.ts
│  │  │  ├─ recordsApi.ts
│  │  │  ├─ usersApi.ts
│  │  │  ├─ workspaceApi.ts
│  │  │  └─ workspaceMemberApi.ts
│  │  ├─ components/                  # React components
│  │  │  ├─ advisor/                  # RAG advisor UI
│  │  │  │  ├─ AdvisorChat.tsx
│  │  │  │  ├─ AgentChat.tsx          # AI Agent UI
│  │  │  │  ├─ AgentConfirmModal.tsx  # Confirmation modal
│  │  │  │  └─ FinancialInsights.tsx
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
│  │  │  ├─ advisor/                  # AI Advisor page
│  │  │  │  └─ index.tsx              # Tabbed UI (Advisor + Agent)
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

# AI Configuration (Hybrid Provider Setup)
# Gemini 3.6 Flash - For receipt OCR/image understanding (superior document processing, 12% faster)
GEMINI_API_KEY=your_gemini_api_key_from_ai_google_dev
GEMINI_MODEL=gemini-3.6-flash

# Groq - For categorization & insights (280 tokens/sec, ~0.95s time-to-first-token)
GROQ_API_KEY=your_groq_api_key_from_console_groq_com
GROQ_MODEL=llama-3.3-70b-versatile

AI_MAX_CATEGORIZATION_REQUESTS_PER_DAY=100
AI_MAX_RECEIPT_UPLOADS_PER_DAY=20

# Cloudinary Configuration (Image Storage)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name

# Application Configuration
APP_BASE_URL=http://localhost:5173
```

**Note:** Use `backend/.env.example` as a reference template.

**Important:** To ensure proper character encoding (₹ rupee symbol, etc.), add the following to `pom.xml`:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### Frontend Environment Variables

The frontend uses a centralized API client for all backend requests. See [frontend/API_CONFIGURATION.md](frontend/API_CONFIGURATION.md) for detailed documentation.

**Development (`.env.local`):**
```env
# Uses Vite proxy to avoid CORS issues
VITE_API_BASE_URL=/api
```

**Production (`.env` and `.env.production`):**
```env
# Direct backend URL
VITE_API_BASE_URL=https://ledgera-backend.onrender.com/api
```

**Key Features:**
- ✅ Centralized Axios client with automatic authentication
- ✅ Consistent error handling across all API calls
- ✅ Environment-based configuration (dev/prod)
- ✅ No hardcoded URLs or direct fetch calls
- ✅ 30-second timeout for all requests
- ✅ Automatic 401 handling with redirect to login

For production deployments:
- Set `VITE_API_BASE_URL` to your backend URL in your hosting platform
- All API calls automatically use this centralized configuration

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

### AI Features (`/api/ai`)
- `POST /api/ai/categorize` — AI-powered transaction categorization
- `POST /api/ai/receipt` — Upload receipt for OCR and auto-extraction
- `GET /api/ai/insights` — Get AI-generated financial insights
- `POST /api/ai/agent` — AI agent tool-calling loop (query, analyze, create)
- `POST /api/ai/agent/confirm` — Confirm pending agent action
- `POST /api/ai/agent/cancel` — Cancel pending agent action
- `GET /api/ai/health` — Check AI service availability

### Financial Advisor (`/api/advisor`)
- `POST /api/advisor/chat` — Chat with RAG-powered financial advisor
- `POST /api/advisor/insights/generate` — Generate personalized insights
- `GET /api/advisor/insights` — Retrieve stored insights

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
   - `VITE_API_BASE_URL` (your backend URL, e.g., `https://your-backend.onrender.com/api`)
4. **Deploy**

### Email Configuration (Resend)

1. **Sign up** at [resend.com](https://resend.com)
2. **Verify your domain** or use `onboarding@resend.dev` for testing
3. **Generate API key** and add to backend environment variables
4. **Configure email templates** in `EmailService.java`

### AI Configuration (Hybrid Provider Setup)

Ledgera uses a **hybrid AI provider strategy** for optimal quota management:

#### Groq (Categorization & Insights)

1. **Sign up for free** at [https://console.groq.com](https://console.groq.com)
2. **Generate API key** from dashboard
3. **Add to backend `.env`**:
   ```env
   GROQ_API_KEY=your_groq_api_key
   GROQ_MODEL=llama-3.3-70b-versatile
   ```
4. **Free tier includes**: Very generous rate limits, minimal daily restrictions
5. **Features enabled**: Transaction categorization, Financial insights

#### Gemini 3.6 Flash (Receipt OCR)

1. **Get your free API key** at [https://aistudio.google.com/apikey](https://aistudio.google.com/apikey)
2. **Sign in** with your Google account
3. **Click "Create API Key"** and select your project (or create new one)
4. **Copy the API key** (starts with `AIza...`)
5. **Add to backend `.env`**:
   ```env
   GEMINI_API_KEY=your_actual_api_key_here
   GEMINI_MODEL=gemini-3.6-flash
   ```
6. **Free tier includes**: 15 requests/minute quota
7. **Features enabled**: Receipt OCR with enhanced document processing (12% faster than Gemini 2.0)

**Why hybrid AI architecture?**
- **Groq** handles high-frequency text tasks (categorization @ 280 tokens/sec, insights with ~0.95s latency) with generous free quota
- **Gemini 3.6 Flash** handles occasional receipt uploads with superior OCR accuracy and document understanding
- **Local embeddings (DJL)** for RAG financial advisor - completely free, no API calls
- **Zero quota exhaustion** during demos or typical usage patterns

📚 **See [backend/AI_PROVIDER_ARCHITECTURE.md](backend/AI_PROVIDER_ARCHITECTURE.md) for detailed architecture documentation**

### Cloudinary Configuration (Image Storage)

1. **Sign up for free** at [https://cloudinary.com/users/register_free](https://cloudinary.com/users/register_free)
2. **Get your credentials** from the dashboard:
   - Cloud Name
   - API Key
   - API Secret
3. **Add to backend `.env`**:
   ```env
   CLOUDINARY_CLOUD_NAME=your_cloud_name
   CLOUDINARY_API_KEY=your_api_key
   CLOUDINARY_API_SECRET=your_api_secret
   ```
4. **Free tier includes**: 25GB storage, 25GB bandwidth/month, unlimited transformations
5. **Features enabled**: Receipt image storage, CDN delivery, automatic optimization

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
