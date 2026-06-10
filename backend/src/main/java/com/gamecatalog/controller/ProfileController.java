package com.gamecatalog.controller;

import com.gamecatalog.dto.UserProfileDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.service.CurrentUserService;
import com.gamecatalog.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// EN: Profile API for the authenticated user.
// RU: API профиля авторизованного пользователя.
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final CurrentUserService currentUserService;
    private final ProfileService profileService;

    public ProfileController(CurrentUserService currentUserService, ProfileService profileService) {
        this.currentUserService = currentUserService;
        this.profileService = profileService;
    }

    // EN: Returns current user details plus their comments and favorite games.
    // RU: Возвращает данные текущего пользователя, его комментарии и избранные игры.
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile() {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(profileService.getProfile(user));
    }
}
