package com.gamecatalog.controller;

import com.gamecatalog.dto.AuthRequestDTO;
import com.gamecatalog.dto.AuthResponseDTO;
import com.gamecatalog.dto.ApiErrorDTO;
import com.gamecatalog.dto.CurrentUserDTO;
import com.gamecatalog.dto.RegisterRequestDTO;
import com.gamecatalog.mapper.UserMapper;
import com.gamecatalog.service.AuthService;
import com.gamecatalog.service.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// EN: Authentication API: register, login, and "who am I" endpoints for the frontend.
// RU: API авторизации: регистрация, вход и endpoint "кто я" для фронтенда.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, CurrentUserService currentUserService, UserMapper userMapper) {
        this.authService = authService;
        this.currentUserService = currentUserService;
        this.userMapper = userMapper;
    }

    // EN: Creates a user account and returns JWT credentials expected by the React app.
    // RU: Создаёт аккаунт пользователя и возвращает JWT-данные, ожидаемые React-приложением.
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            if ("Username is already taken".equals(e.getMessage())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw e;
        }
    }

    // EN: Authenticates existing users and maps bad credentials to HTTP 401.
    // RU: Авторизует существующих пользователей и превращает неверные данные в HTTP 401.
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    // EN: Returns the current user's id and username based on the JWT token.
    // RU: Возвращает id и username текущего пользователя на основе JWT-токена.
    @GetMapping("/me")
    public ResponseEntity<CurrentUserDTO> getCurrentUser() {
        return currentUserService.getCurrentUser()
                .map(userMapper::toCurrentUserDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    // EN: Keeps validation errors in a consistent JSON shape for the frontend.
    // RU: Возвращает ошибки валидации в едином JSON-формате для фронтенда.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ApiErrorDTO(e.getMessage()));
    }

    // EN: Converts explicit response statuses (409/401/etc.) into the same error DTO.
    // RU: Преобразует явные статусы ответа (409/401 и т.д.) в тот же error DTO.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> handleStatus(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new ApiErrorDTO(e.getReason()));
    }
}
