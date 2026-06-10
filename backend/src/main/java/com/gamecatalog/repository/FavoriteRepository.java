package com.gamecatalog.repository;

import com.gamecatalog.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// EN: Database queries for favorite relations between users and products.
// RU: Запросы к базе для связей избранного между пользователями и продуктами.
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    List<Favorite> findByUserIdOrderByCreationDateDesc(Long userId);
}
