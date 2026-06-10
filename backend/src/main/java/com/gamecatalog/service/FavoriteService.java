package com.gamecatalog.service;

import com.gamecatalog.dto.FavoriteStatusDTO;
import com.gamecatalog.dto.ProductDTO;
import com.gamecatalog.entity.Favorite;
import com.gamecatalog.entity.Product;
import com.gamecatalog.mapper.ProductMapper;
import com.gamecatalog.repository.FavoriteRepository;
import com.gamecatalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// EN: Business layer for adding/removing products from a user's favorites.
// RU: Бизнес-слой для добавления/удаления продуктов из избранного пользователя.
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository,
                           ProductMapper productMapper) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // EN: Checks whether the favorite relation exists without changing data.
    // RU: Проверяет существование связи избранного без изменения данных.
    public FavoriteStatusDTO getStatus(Long productId, Long userId) {
        boolean favorite = favoriteRepository.existsByUserIdAndProductId(userId, productId);
        return new FavoriteStatusDTO(productId, favorite);
    }

    // EN: Creates the favorite relation if it does not already exist.
    // RU: Создаёт связь избранного, если она ещё не существует.
    @Transactional
    public FavoriteStatusDTO addFavorite(Long productId, Long userId) {
        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            favoriteRepository.save(new Favorite(userId, product));
        }

        return new FavoriteStatusDTO(productId, true);
    }

    // EN: Deletes the favorite relation and returns the new "not favorite" state.
    // RU: Удаляет связь избранного и возвращает новое состояние "не в избранном".
    @Transactional
    public FavoriteStatusDTO removeFavorite(Long productId, Long userId) {
        favoriteRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(favoriteRepository::delete);
        return new FavoriteStatusDTO(productId, false);
    }

    // EN: Used by the profile screen to show the user's favorite products.
    // RU: Используется экраном профиля, чтобы показать избранные продукты пользователя.
    public List<ProductDTO> getFavoriteProducts(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreationDateDesc(userId)
                .stream()
                .map(Favorite::getProduct)
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}
