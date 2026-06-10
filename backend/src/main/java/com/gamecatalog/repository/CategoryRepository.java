package com.gamecatalog.repository;

import com.gamecatalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// EN: Database access for game categories/genres.
// RU: Доступ к базе для категорий/жанров игр.
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsDeletedFalse();
    Optional<Category> findByNameAndIsDeletedFalse(String name);
}
