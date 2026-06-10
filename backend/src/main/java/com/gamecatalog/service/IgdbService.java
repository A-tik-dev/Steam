package com.gamecatalog.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.dto.GenreDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

// EN: Talks to IGDB and syncs fetched games into the local database.
// RU: Обращается к IGDB и синхронизирует полученные игры в локальную базу.
@Service
public class IgdbService {
    private static final String GAME_FIELDS =
            "fields id, name, summary, cover.image_id, rating, release_dates.date, genres.id, genres.name; ";

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

    // EN: Fetches a Twitch OAuth token for IGDB requests.
    // RU: Получает OAuth-токен Twitch для запросов к IGDB.
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

    // EN: Builds the IGDB request headers.
    // RU: Собирает заголовки для запросов к IGDB.
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

    // EN: Loads popular games and saves new ones locally.
    // RU: Загружает популярные игры и сохраняет новые записи локально.
    public List<GameDTO> getPopularGames(int limit, int offset) {
        String query = GAME_FIELDS +
                      "where rating > 70 & rating_count > 10; " +
                      "sort rating desc; " +
                      "limit " + limit + "; " +
                      "offset " + offset + ";";

        return fetchAndSyncGames(query, "Failed to fetch games from IGDB");
    }

    // EN: Searches IGDB by text and syncs the result set locally.
    // RU: Ищет игры в IGDB по тексту и синхронизирует найденные записи локально.
    public List<GameDTO> searchGames(String searchTerm, int limit) {
        String query = "search \"" + searchTerm + "\"; " +
                      GAME_FIELDS +
                      "limit " + limit + ";";

        return fetchAndSyncGames(query, "Failed to search games");
    }

    // EN: Loads IGDB's genre list for the frontend category selector.
    // RU: Загружает список жанров IGDB для селектора категорий на фронтенде.
    public List<GenreDTO> getGenres() {
        String query = "fields id, name; sort name asc; limit 100;";
        HttpEntity<String> entity = new HttpEntity<>(query, createHeaders());

        try {
            ResponseEntity<GenreDTO[]> response = restTemplate.exchange(
                    apiUrl + "/genres",
                    HttpMethod.POST,
                    entity,
                    GenreDTO[].class
            );

            return response.getBody() == null ? List.of() : Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch genres from IGDB", e);
        }
    }

    // EN: Loads one page of games from IGDB that belong to the selected genre.
    // RU: Загружает одну страницу игр IGDB, которые относятся к выбранному жанру.
    public List<GameDTO> getGamesByGenre(Long genreId, int limit, int offset) {
        String query = GAME_FIELDS +
                "where genres = (" + genreId + ") & rating > 70 & rating_count > 10; " +
                "sort rating desc; " +
                "limit " + limit + "; " +
                "offset " + offset + ";";

        return fetchAndSyncGames(query, "Failed to fetch games by genre from IGDB");
    }

    // EN: Executes an IGDB game query and keeps local products in sync for details/comments.
    // RU: Выполняет запрос игр к IGDB и синхронизирует локальные продукты для деталей/комментариев.
    private List<GameDTO> fetchAndSyncGames(String query, String errorMessage) {
        HttpEntity<String> entity = new HttpEntity<>(query, createHeaders());

        try {
            ResponseEntity<GameDTO[]> response = restTemplate.exchange(
                    apiUrl + "/games",
                    HttpMethod.POST,
                    entity,
                    GameDTO[].class
            );

            List<GameDTO> games = response.getBody() == null ? List.of() : Arrays.asList(response.getBody());

            for (GameDTO game : games) {
                productService.saveGameWithReviews(game);
            }

            return games;
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
    }
}
