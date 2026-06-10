package com.gamecatalog.controller;

import com.gamecatalog.dto.FavoriteStatusDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.service.CurrentUserService;
import com.gamecatalog.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// EN: Favorite API for the current user; every method requires a valid JWT.
// RU: API избранного для текущего пользователя; каждый метод требует валидный JWT.
@RestController
@RequestMapping("/api/products")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final CurrentUserService currentUserService;

    public FavoriteController(FavoriteService favoriteService, CurrentUserService currentUserService) {
        this.favoriteService = favoriteService;
        this.currentUserService = currentUserService;
    }

    // EN: Returns whether the current user has this product in favorites.
    // RU: Возвращает, есть ли этот продукт в избранном у текущего пользователя.
    @GetMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteStatusDTO> getFavoriteStatus(@PathVariable Long productId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(favoriteService.getStatus(productId, user.getId()));
    }

    // EN: Adds the product to the current user's favorites.
    // RU: Добавляет продукт в избранное текущего пользователя.
    @PostMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteStatusDTO> addFavorite(@PathVariable Long productId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(favoriteService.addFavorite(productId, user.getId()));
    }

    // EN: Removes the product from the current user's favorites.
    // RU: Убирает продукт из избранного текущего пользователя.
    @DeleteMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteStatusDTO> removeFavorite(@PathVariable Long productId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(favoriteService.removeFavorite(productId, user.getId()));
    }
}
