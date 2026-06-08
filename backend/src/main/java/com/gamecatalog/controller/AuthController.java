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

    // Returns the current user's id and username based on the JWT token.
    @GetMapping("/me")
    public ResponseEntity<CurrentUserDTO> getCurrentUser() {
        return currentUserService.getCurrentUser()
                .map(userMapper::toCurrentUserDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ApiErrorDTO(e.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> handleStatus(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new ApiErrorDTO(e.getReason()));
    }
}
