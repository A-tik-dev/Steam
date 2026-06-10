package com.gamecatalog.config;

import com.gamecatalog.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// EN: Wires Spring Security beans: user lookup, password hashing, and authentication manager.
// RU: Подключает Spring Security beans: поиск пользователя, хеширование паролей и authentication manager.
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // EN: Loads active users from the database for login and JWT validation.
    // RU: Загружает активных пользователей из базы для входа и проверки JWT.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsernameAndIsDeletedFalse(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // EN: Connects the user details service with BCrypt password verification.
    // RU: Соединяет user details service с проверкой BCrypt-паролей.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // EN: Exposes Spring's authentication manager for AuthService.login().
    // RU: Открывает Spring authentication manager для AuthService.login().
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // EN: Hashes new passwords and verifies login passwords.
    // RU: Хеширует новые пароли и проверяет пароли при входе.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
