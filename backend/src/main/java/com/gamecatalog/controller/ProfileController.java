package com.gamecatalog.controller;

import com.gamecatalog.dto.UserProfileDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.service.CurrentUserService;
import com.gamecatalog.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final CurrentUserService currentUserService;
    private final ProfileService profileService;

    public ProfileController(CurrentUserService currentUserService, ProfileService profileService) {
        this.currentUserService = currentUserService;
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile() {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(profileService.getProfile(user));
    }
}
