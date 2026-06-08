package com.gamecatalog.service;

import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.entity.Product;
import com.gamecatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private SeedReviewService seedReviewService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository, seedReviewService);
    }

    @Test
    void shouldSaveGameAndAskSeedServiceToCreateReviews() {
        GameDTO game = new GameDTO();
        game.setId(123L);
        game.setName("Test Game");
        game.setSummary("Test summary");
        game.setRating(90.0);

        when(productRepository.findByIgdbId(123L)).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(55L);
            return product;
        });

        Product saved = productService.saveGameWithReviews(game);

        assertEquals(55L, saved.getId());
        verify(seedReviewService).createReviews(saved, 90.0);
    }
}
