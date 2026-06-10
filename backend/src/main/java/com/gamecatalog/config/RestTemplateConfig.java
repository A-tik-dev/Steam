package com.gamecatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// EN: Shared HTTP client used for outbound API calls to IGDB/Twitch.
// RU: Общий HTTP-клиент для внешних запросов к IGDB/Twitch.
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
