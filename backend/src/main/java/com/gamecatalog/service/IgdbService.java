package com.gamecatalog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamecatalog.dto.GameDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

// Talks to IGDB and syncs fetched games into the local database.
@Service
public class IgdbService {

    @Value("${igdb.client.id}")
    private String clientId;

    @Value("${igdb.client.secret}")
    private String clientSecret;

    @Value("${igdb.api.url}")
    private String apiUrl;

    @Value("${igdb.auth.url}")
    private String authUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private String accessToken;

    public IgdbService(RestTemplate restTemplate, ProductService productService) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        this.productService = productService;
    }

    // Fetches a Twitch OAuth token for IGDB requests.
    private void authenticate() {
        String url = authUrl + "?client_id=" + clientId +
                     "&client_secret=" + clientSecret +
                     "&grant_type=client_credentials";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            accessToken = jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate with IGDB", e);
        }
    }

    // Builds the IGDB request headers.
    private HttpHeaders createHeaders() {
        if (accessToken == null) {
            authenticate();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-ID", clientId);
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.TEXT_PLAIN);
        return headers;
    }

    // Loads popular games and saves new ones locally.
    public List<GameDTO> getPopularGames(int limit, int offset) {
        String query = "fields id, name, summary, cover.image_id, rating, release_dates.date, genres.name; " +
                      "where rating > 70 & rating_count > 10; " +
                      "sort rating desc; " +
                      "limit " + limit + "; " +
                      "offset " + offset + ";";

        HttpEntity<String> entity = new HttpEntity<>(query, createHeaders());

        try {
            ResponseEntity<GameDTO[]> response = restTemplate.exchange(
                apiUrl + "/games",
                HttpMethod.POST,
                entity,
                GameDTO[].class
            );

            List<GameDTO> games = Arrays.asList(response.getBody());

            for (GameDTO game : games) {
                productService.saveGameWithReviews(game);
            }

            return games;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch games from IGDB", e);
        }
    }

    // Searches IGDB by text and syncs the result set locally.
    public List<GameDTO> searchGames(String searchTerm, int limit) {
        String query = "search \"" + searchTerm + "\"; " +
                      "fields id, name, summary, cover.image_id, rating, release_dates.date, genres.name; " +
                      "limit " + limit + ";";

        HttpEntity<String> entity = new HttpEntity<>(query, createHeaders());

        try {
            ResponseEntity<GameDTO[]> response = restTemplate.exchange(
                apiUrl + "/games",
                HttpMethod.POST,
                entity,
                GameDTO[].class
            );

            List<GameDTO> games = Arrays.asList(response.getBody());

            for (GameDTO game : games) {
                productService.saveGameWithReviews(game);
            }

            return games;
        } catch (Exception e) {
            throw new RuntimeException("Failed to search games", e);
        }
    }
}
