package com.gamecatalog.repository;

import com.gamecatalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// EN: Database access for local products imported from IGDB.
// RU: Доступ к базе для локальных продуктов, импортированных из IGDB.
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsDeletedFalse();
    List<Product> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);
    Product findByIgdbId(Long igdbId);
}
