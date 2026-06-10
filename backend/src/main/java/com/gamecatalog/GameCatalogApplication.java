package com.gamecatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the backend application.
 * Spring Boot bootstraps the web server and all configured beans from here.
 *
 * RU: Точка входа backend-приложения.
 * RU: Spring Boot запускает отсюда веб-сервер и все настроенные beans.
 */
@SpringBootApplication
public class GameCatalogApplication {
    /**
     * Starts the Spring application context.
     *
     * RU: Запускает Spring application context.
     */
    public static void main(String[] args) {
        SpringApplication.run(GameCatalogApplication.class, args);
    }
}
