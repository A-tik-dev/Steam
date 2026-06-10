package com.gamecatalog.repository;

import com.gamecatalog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// EN: Database access for active users used by authentication and display names.
// RU: Доступ к базе для активных пользователей, авторизации и отображения имён.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    boolean existsByUsername(String username);
}
