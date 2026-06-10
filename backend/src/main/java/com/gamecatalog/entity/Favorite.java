package com.gamecatalog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// EN: Join entity that stores one user's favorite game/product.
// RU: Связующая entity, которая хранит одну избранную игру/продукт пользователя.
@Entity
@Table(
    name = "favorite",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"})
)
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    public Favorite() {}

    public Favorite(Long userId, Product product) {
        this.userId = userId;
        this.product = product;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
