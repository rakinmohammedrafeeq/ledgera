package com.ledgera;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgeraApplication {

    public static void main(String[] args) {

        try {
            Dotenv dotenv = Dotenv.load();

            // Database
            System.setProperty("spring.datasource.url", dotenv.get("DB_URL", ""));
            System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME", ""));
            System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD", ""));

            // JWT
            System.setProperty("jwt.secret", dotenv.get("JWT_SECRET", ""));
            System.setProperty("jwt.expiration", dotenv.get("JWT_EXPIRATION", "86400000"));

        } catch (Exception ignored) {}

        SpringApplication.run(LedgeraApplication.class, args);
    }
}