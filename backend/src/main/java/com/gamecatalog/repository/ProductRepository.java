package com.gamecatalog.repository;

import com.gamecatalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Database access for products.
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsDeletedFalse();
    List<Product> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title);
    Product findByIgdbId(Long igdbId);
}
