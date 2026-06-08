package com.gamecatalog.controller;

import com.gamecatalog.dto.FavoriteStatusDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.service.CurrentUserService;
import com.gamecatalog.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final CurrentUserService currentUserService;

    public FavoriteController(FavoriteService favoriteService, CurrentUserService currentUserService) {
        this.favoriteService = favoriteService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteStatusDTO> getFavoriteStatus(@PathVariable Long productId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(favoriteService.getStatus(productId, user.getId()));
    }

    @PostMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteStatusDTO> addFavorite(@PathVariable Long productId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(favoriteService.addFavorite(productId, user.getId()));
    }

    @DeleteMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteStatusDTO> removeFavorite(@PathVariable Long productId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(favoriteService.removeFavorite(productId, user.getId()));
    }
}
