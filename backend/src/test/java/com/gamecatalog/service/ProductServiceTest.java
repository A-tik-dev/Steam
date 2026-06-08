package com.gamecatalog.service;

import com.gamecatalog.dto.GameDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.CommentRepository;
import com.gamecatalog.repository.ProductRepository;
import com.gamecatalog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(
                productRepository,
                commentRepository,
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldCreateSeededReviewsWithMockReviewersInsteadOfSystemUser() {
        GameDTO game = new GameDTO();
        game.setId(123L);
        game.setName("Test Game");
        game.setSummary("Test summary");
        game.setRating(90.0);

        AtomicLong userIds = new AtomicLong(10L);

        when(productRepository.findByIgdbId(123L)).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(55L);
            return product;
        });
        when(userRepository.findByUsernameAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userIds.getAndIncrement());
            return user;
        });
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        productService.saveGameWithReviews(game);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<Comment>> commentsCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(commentRepository).saveAll(commentsCaptor.capture());

        List<Comment> comments = new ArrayList<>();
        commentsCaptor.getValue().forEach(comments::add);

        assertFalse(comments.isEmpty());
        assertTrue(comments.stream().noneMatch(comment -> Long.valueOf(1L).equals(comment.getCreatorUserId())));
    }

    @Test
    void shouldDeleteOwnComment() {
        Product product = new Product("Test Game", "Test summary", 1L, null);
        Comment comment = new Comment("My review", 42L, product);
        comment.setId(5L);

        when(commentRepository.findByIdAndCreatorUserIdAndIsDeletedFalse(5L, 42L))
                .thenReturn(Optional.of(comment));

        boolean deleted = productService.deleteCommentByOwner(5L, 42L);

        assertTrue(deleted);
        assertTrue(comment.getIsDeleted());
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldNotDeleteAnotherUsersComment() {
        when(commentRepository.findByIdAndCreatorUserIdAndIsDeletedFalse(5L, 99L))
                .thenReturn(Optional.empty());

        boolean deleted = productService.deleteCommentByOwner(5L, 99L);

        assertFalse(deleted);
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
