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

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    private void validateCredentials(String username, String password) {
        if (username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }
    }
}
