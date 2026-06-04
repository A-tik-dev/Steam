package com.gamecatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the backend application.
 * Spring Boot bootstraps the web server and all configured beans from here.
 */
@SpringBootApplication
public class GameCatalogApplication {
    /**
     * Starts the Spring application context.
     */
    public static void main(String[] args) {
        SpringApplication.run(GameCatalogApplication.class, args);
    }
}
