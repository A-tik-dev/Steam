package com.gamecatalog.repository;

import com.gamecatalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Database access for categories.
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsDeletedFalse();
    Optional<Category> findByNameAndIsDeletedFalse(String name);
}
