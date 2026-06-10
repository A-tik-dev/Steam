package com.gamecatalog.service;

import com.gamecatalog.entity.User;
import com.gamecatalog.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// EN: Reads the authenticated user from Spring Security and resolves it to the local User entity.
// RU: Читает авторизованного пользователя из Spring Security и находит его локальную User entity.
@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // EN: Empty means the request is anonymous or the account was deleted.
    // RU: Empty означает, что запрос анонимный или аккаунт был удалён.
    public Optional<User> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }

        return userRepository.findByUsernameAndIsDeletedFalse(auth.getName());
    }
}
