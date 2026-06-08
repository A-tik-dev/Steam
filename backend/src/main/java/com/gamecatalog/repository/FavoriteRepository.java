package com.gamecatalog.repository;

import com.gamecatalog.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    List<Favorite> findByUserIdOrderByCreationDateDesc(Long userId);
}
