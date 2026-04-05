# ============================================================
# Multi-Stage Dockerfile for Ledgera Backend (Spring Boot 3.2.5)
# Java 17 | Maven | Production-Ready
# ============================================================

# Stage 1: Build Stage (Maven build with JDK)
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy only pom.xml to leverage Docker layer caching
COPY pom.xml .

# Download dependencies (separate layer for better caching)
RUN mvn dependency:go-offline -q

# Copy source code
COPY src ./src

# Build the application (skip tests for production builds)
RUN mvn clean package -q -DskipTests

# ============================================================
# Stage 2: Runtime Stage (lightweight JRE image)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user for security
RUN useradd -m -u 1001 appuser && \
    chown -R appuser:appuser /app

# Copy built JAR from builder stage
COPY --from=builder --chown=appuser:appuser /build/target/ledgera-backend-*.jar app.jar

# Switch to non-root user
USER appuser

# Expose port (can be overridden at runtime)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application with PORT environment variable support
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]

