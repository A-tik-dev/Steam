package com.gamecatalog.service;

import com.gamecatalog.config.JwtService;
import com.gamecatalog.dto.AuthRequestDTO;
import com.gamecatalog.dto.AuthResponseDTO;
import com.gamecatalog.dto.RegisterRequestDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// EN: Business logic for registration and login; returns JWT payloads used by the frontend.
// RU: Бизнес-логика регистрации и входа; возвращает JWT-ответы, которые использует фронтенд.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    // EN: Creates a new local user, hashes the password, and immediately issues a JWT.
    // RU: Создаёт нового локального пользователя, хеширует пароль и сразу выдаёт JWT.
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String username = normalizeUsername(request.getUsername());
        validateCredentials(username, request.getPassword());

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken");
        }

        User user = new User(
                username,
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER"
        );
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponseDTO(jwtToken, user.getUsername());
    }

    // EN: Checks credentials through Spring Security and creates a fresh JWT on success.
    // RU: Проверяет логин/пароль через Spring Security и создаёт новый JWT при успехе.
    public AuthResponseDTO login(AuthRequestDTO request) {
        String username = normalizeUsername(request.getUsername());
        validateCredentials(username, request.getPassword());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponseDTO(jwtToken, username);
    }

    // EN: Keeps username validation consistent for both register and login.
    // RU: Делает проверку username одинаковой для регистрации и входа.
    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    private void validateCredentials(String username, String password) {
        if (username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }
    }
}
