package com.ledgera;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgeraApplication {

    public static void main(String[] args) {

        try {
            Dotenv dotenv = Dotenv.load();

            // =========================
            // DATABASE
            // =========================
            System.setProperty(
                    "spring.datasource.url",
                    dotenv.get("DB_URL", "")
            );

            System.setProperty(
                    "spring.datasource.username",
                    dotenv.get("DB_USERNAME", "")
            );

            System.setProperty(
                    "spring.datasource.password",
                    dotenv.get("DB_PASSWORD", "")
            );

            // =========================
            // JWT
            // =========================
            System.setProperty(
                    "jwt.secret",
                    dotenv.get("JWT_SECRET", "")
            );

            System.setProperty(
                    "jwt.expiration",
                    dotenv.get("JWT_EXPIRATION", "86400000")
            );

            // =========================
            // RESEND EMAIL
            // =========================
            System.setProperty(
                    "RESEND_API_KEY",
                    dotenv.get("RESEND_API_KEY", "")
            );

            System.setProperty(
                    "RESEND_FROM_EMAIL",
                    dotenv.get(
                            "RESEND_FROM_EMAIL",
                            "rakinmohammedrafeeq@gmail.com"
                    )
            );

            System.setProperty(
                    "RESEND_FROM_NAME",
                    dotenv.get(
                            "RESEND_FROM_NAME",
                            "Ledgera"
                    )
            );

            // =========================
            // APP URL
            // =========================
            System.setProperty(
                    "APP_BASE_URL",
                    dotenv.get(
                            "APP_BASE_URL",
                            "http://localhost:5173"
                    )
            );

        } catch (Exception e) {
            System.out.println("Could not load .env file");
        }

        SpringApplication.run(LedgeraApplication.class, args);
    }
}