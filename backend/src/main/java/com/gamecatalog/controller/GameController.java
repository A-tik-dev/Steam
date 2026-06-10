package com.gamecatalog.controller;

import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.dto.GenreDTO;
import com.gamecatalog.service.IgdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// EN: Exposes IGDB-backed game discovery endpoints.
// RU: Открывает endpoints поиска и просмотра игр, которые получают данные из IGDB.
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IgdbService igdbService;

    public GameController(IgdbService igdbService) {
        this.igdbService = igdbService;
    }

    // EN: Returns a page of popular games and syncs them locally.
    // RU: Возвращает страницу популярных игр и синхронизирует их в локальную базу.
    @GetMapping("/popular")
    public ResponseEntity<List<GameDTO>> getPopularGames(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(igdbService.getPopularGames(limit, offset));
    }

    // EN: Returns all IGDB genres so the frontend can build a real category selector.
    // RU: Возвращает все жанры IGDB, чтобы фронтенд мог построить настоящий выбор категорий.
    @GetMapping("/genres")
    public ResponseEntity<List<GenreDTO>> getGenres() {
        return ResponseEntity.ok(igdbService.getGenres());
    }

    // EN: Returns a full page of popular games for one selected IGDB genre.
    // RU: Возвращает полноценную страницу популярных игр для выбранного жанра IGDB.
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<GameDTO>> getGamesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(igdbService.getGamesByGenre(genreId, limit, offset));
    }

    // EN: Searches games in IGDB by text query.
    // RU: Ищет игры в IGDB по текстовому запросу.
    @GetMapping("/search")
    public ResponseEntity<List<GameDTO>> searchGames(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(igdbService.searchGames(query, limit));
    }
}
