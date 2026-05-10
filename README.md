<div align="center">
  <img src="public/icon.svg" alt="Ledgera Logo" width="120" height="120">
  
  # Ledgera
  
  **Full-Stack Finance Tracking System**
  
  [![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
  [![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
  [![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue.svg)](https://www.typescriptlang.org/)
</div>

---

Ledgera is a full-stack finance tracking and analytics application with role-based access control, JWT authentication, and interactive dashboard visualizations.

## Features

### Authentication & Security
- JWT-based authentication and authorization
- Password reset flow with email integration (Resend API)
- Rate limiting for password reset requests (3 per 15 minutes)
- Role-based access control (Admin/User)
- Secure token management with 15-minute expiry

### Financial Management
- Income and expense tracking
- Advanced filtering and search
- Category-based organization
- Transaction history
- Export and reporting

### Analytics & Visualization
- Interactive dashboard with real-time analytics
- Monthly trend charts
- Category-wise spending breakdown
- Income vs expense comparisons
- Recent activity feed

### Architecture
- RESTful API with Spring Boot
- Layered architecture (Controller → Service → Repository)
- Database migrations with Flyway
- Comprehensive error handling
- Detailed logging and monitoring  

---

## Why Ledgera?

Ledgera demonstrates production-grade full-stack development practices:

- **Enterprise Architecture** — Layered backend design with clear separation of concerns
- **Security First** — JWT authentication, role-based access control, rate limiting
- **Modern Stack** — Spring Boot 3, React 18, TypeScript, PostgreSQL
- **Email Integration** — Professional password reset flow with Resend API
- **Scalable Design** — RESTful API, database migrations, comprehensive error handling
- **Developer Experience** — Hot reload, TypeScript, ESLint, detailed logging

Built to reflect production-level design practices used in modern full-stack applications.

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
│  │  │  ├─ AuthController.java
│  │  │  ├─ DashboardController.java
│  │  │  ├─ FinancialRecordController.java
│  │  │  ├─ HealthController.java
│  │  │  └─ UserController.java
│  │  ├─ dto/                         # Data Transfer Objects
│  │  ├─ entity/                      # JPA entities
│  │  │  ├─ FinancialRecord.java
│  │  │  └─ User.java
│  │  ├─ enums/                       # Enumerations
│  │  │  ├─ Role.java
│  │  │  └─ TransactionType.java
│  │  ├─ exception/                   # Exception handling
│  │  │  └─ GlobalExceptionHandler.java
│  │  ├─ repository/                  # Data access layer
│  │  │  ├─ FinancialRecordRepository.java
│  │  │  ├─ FinancialRecordSpecification.java
│  │  │  └─ UserRepository.java
│  │  ├─ security/                    # Security components
│  │  │  ├─ CustomUserDetailsService.java
│  │  │  ├─ JwtAuthenticationFilter.java
│  │  │  └─ JwtTokenProvider.java
│  │  └─ service/                     # Business logic
│  │     ├─ AuthService.java
│  │     ├─ CurrentUserService.java
│  │     ├─ DashboardService.java
│  │     ├─ EmailService.java
│  │     ├─ FinancialRecordService.java
│  │     └─ UserService.java
│  ├─ src/main/resources/
│  │  ├─ application.properties       # Main config
│  │  ├─ application-h2.properties    # H2 profile
│  │  └─ db/migration/                # Flyway migrations
│  │     ├─ V1__init.sql
│  │     └─ V2__backfill_financial_record_users.sql
│  ├─ .env                            # Environment variables
│  ├─ .env.example                    # Environment template
│  ├─ Dockerfile                      # Docker configuration
│  ├─ docker-compose.yml              # Docker Compose
│  └─ pom.xml                         # Maven dependencies
│
├─ frontend/                          # React + Vite SPA
│  ├─ src/
│  │  ├─ api/                         # API client
│  │  │  ├─ authApi.ts
│  │  │  ├─ client.ts
│  │  │  ├─ dashboardApi.ts
│  │  │  ├─ recordsApi.ts
│  │  │  └─ usersApi.ts
│  │  ├─ components/                  # React components
│  │  │  ├─ auth/                     # Auth components
│  │  │  ├─ backend/                  # Backend status
│  │  │  ├─ dashboard/                # Dashboard widgets
│  │  │  ├─ landing/                  # Landing page
│  │  │  ├─ layout/                   # Layout components
│  │  │  ├─ records/                  # Record components
│  │  │  └─ ui/                       # UI primitives
│  │  ├─ config/                      # Configuration
│  │  │  └─ brandAssets.ts            # Logo & branding
│  │  ├─ contexts/                    # React contexts
│  │  ├─ hooks/                       # Custom hooks
│  │  ├─ pages/                       # Page components
│  │  │  ├─ auth/                     # Auth pages
│  │  │  ├─ dashboard/                # Dashboard page
│  │  │  ├─ records/                  # Records page
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
- `POST /api/auth/forgot-password` — Request password reset
- `POST /api/auth/reset-password` — Reset password with token

### Users (`/api/users`)
- `GET /api/users/me` — Get current user profile
- `GET /api/users` — List all users (Admin only)
- `PUT /api/users/{id}` — Update user (Admin only)
- `DELETE /api/users/{id}` — Delete user (Admin only)

### Financial Records (`/api/records`)
- `GET /api/records` — List records with filtering
- `POST /api/records` — Create new record
- `GET /api/records/{id}` — Get record by ID
- `PUT /api/records/{id}` — Update record
- `DELETE /api/records/{id}` — Delete record

### Dashboard (`/api/dashboard`)
- `GET /api/dashboard` — Get dashboard analytics and metrics

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
Real-time analytics with monthly trends, category breakdowns, and recent activity.

### Financial Records
Advanced filtering, search, and management of transactions.

### Authentication
Secure login, registration, and password reset flow.

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
