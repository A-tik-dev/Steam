package com.gamecatalog.controller;

import com.gamecatalog.dto.AuthRequestDTO;
import com.gamecatalog.dto.AuthResponseDTO;
import com.gamecatalog.dto.RegisterRequestDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.UserRepository;
import com.gamecatalog.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        } catch (RuntimeException e) {
            if ("Username is already taken".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
            }
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Invalid username or password"));
        }
    }

    // Returns the current user's id and username based on the JWT token.
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        String username = auth.getName();
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        return ResponseEntity.ok(response);
    }

    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
