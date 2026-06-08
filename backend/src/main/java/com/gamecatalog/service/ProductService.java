package com.gamecatalog.service;

import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.entity.Product;
import com.gamecatalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private static final Long SYSTEM_USER_ID = 1L;

    private final ProductRepository productRepository;
    private final SeedReviewService seedReviewService;

    public ProductService(ProductRepository productRepository, SeedReviewService seedReviewService) {
        this.productRepository = productRepository;
        this.seedReviewService = seedReviewService;
    }

    @Transactional
    public Product saveGameWithReviews(GameDTO gameDTO) {
        Product existingProduct = productRepository.findByIgdbId(gameDTO.getId());
        if (existingProduct != null) {
            seedReviewService.repairLegacyOwners(existingProduct.getId());
            return existingProduct;
        }

        Product product = createProductFromGame(gameDTO);
        Product savedProduct = productRepository.save(product);
        seedReviewService.createReviews(savedProduct, gameDTO.getRating());
        return savedProduct;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product getProductByIgdbId(Long igdbId) {
        return productRepository.findByIgdbId(igdbId);
    }

    private Product createProductFromGame(GameDTO gameDTO) {
        Product product = new Product(
                gameDTO.getName(),
                gameDTO.getSummary(),
                SYSTEM_USER_ID,
                gameDTO.getCover() != null ? gameDTO.getCover().getUrl() : null
        );
        product.setIgdbId(gameDTO.getId());
        return product;
    }
}
