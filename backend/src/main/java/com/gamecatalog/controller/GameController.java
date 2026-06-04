package com.gamecatalog.controller;

import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.service.IgdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Exposes IGDB-backed game discovery endpoints.
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IgdbService igdbService;

    public GameController(IgdbService igdbService) {
        this.igdbService = igdbService;
    }

    // Returns a page of popular games and syncs them locally.
    @GetMapping("/popular")
    public ResponseEntity<List<GameDTO>> getPopularGames(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(igdbService.getPopularGames(limit, offset));
    }

    // Searches games in IGDB by text query.
    @GetMapping("/search")
    public ResponseEntity<List<GameDTO>> searchGames(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(igdbService.searchGames(query, limit));
    }
}
