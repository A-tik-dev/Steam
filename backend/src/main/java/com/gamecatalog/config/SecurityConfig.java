package com.gamecatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    // EN: Defines which routes are public and which require a JWT-authenticated user.
    // RU: Определяет, какие маршруты публичные, а какие требуют пользователя с JWT.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(path(HttpMethod.OPTIONS, "/**")).permitAll()
                .requestMatchers(path("/api/auth/**")).permitAll()
                .requestMatchers(path(HttpMethod.GET, "/api/games"), path(HttpMethod.GET, "/api/games/**")).permitAll()
                .requestMatchers(path(HttpMethod.POST, "/api/products/*/comments")).authenticated()
                .requestMatchers(path(HttpMethod.DELETE, "/api/products/comments/*")).authenticated()
                .requestMatchers(path(HttpMethod.GET, "/api/products/*/favorite")).authenticated()
                .requestMatchers(path(HttpMethod.POST, "/api/products/*/favorite")).authenticated()
                .requestMatchers(path(HttpMethod.DELETE, "/api/products/*/favorite")).authenticated()
                .requestMatchers(path("/api/profile/**")).authenticated()
                .requestMatchers(path(HttpMethod.GET, "/api/products"), path(HttpMethod.GET, "/api/products/**")).permitAll()
                .requestMatchers(path("/swagger-ui/**"), path("/v3/api-docs/**"), path("/swagger-ui.html")).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // EN: Small helper keeps Ant-style matcher creation readable.
    // RU: Небольшой helper делает создание Ant-style matcher'ов читаемым.
    private static AntPathRequestMatcher path(String pattern) {
        return new AntPathRequestMatcher(pattern);
    }

    private static AntPathRequestMatcher path(HttpMethod method, String pattern) {
        return new AntPathRequestMatcher(pattern, method.name());
    }

    // EN: Allows the React dev server/browser to call the backend API.
    // RU: Разрешает React dev server/browser обращаться к backend API.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
