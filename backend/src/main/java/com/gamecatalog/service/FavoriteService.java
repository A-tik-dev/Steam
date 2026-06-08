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

    public FavoriteStatusDTO getStatus(Long productId, Long userId) {
        boolean favorite = favoriteRepository.existsByUserIdAndProductId(userId, productId);
        return new FavoriteStatusDTO(productId, favorite);
    }

    @Transactional
    public FavoriteStatusDTO addFavorite(Long productId, Long userId) {
        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            favoriteRepository.save(new Favorite(userId, product));
        }

        return new FavoriteStatusDTO(productId, true);
    }

    @Transactional
    public FavoriteStatusDTO removeFavorite(Long productId, Long userId) {
        favoriteRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(favoriteRepository::delete);
        return new FavoriteStatusDTO(productId, false);
    }

    public List<ProductDTO> getFavoriteProducts(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreationDateDesc(userId)
                .stream()
                .map(Favorite::getProduct)
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}
