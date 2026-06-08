package com.gamecatalog.service;

import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.mapper.CommentMapper;
import com.gamecatalog.repository.CommentRepository;
import com.gamecatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private SeedReviewService seedReviewService;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentService = new CommentService(
                commentRepository,
                productRepository,
                commentMapper,
                seedReviewService
        );
    }

    @Test
    void shouldDeleteOwnComment() {
        Product product = new Product("Test Game", "Test summary", 1L, null);
        Comment comment = new Comment("My review", 42L, product);
        comment.setId(5L);

        when(commentRepository.findByIdAndCreatorUserIdAndIsDeletedFalse(5L, 42L))
                .thenReturn(Optional.of(comment));

        boolean deleted = commentService.deleteCommentByOwner(5L, 42L);

        assertTrue(deleted);
        assertTrue(comment.getIsDeleted());
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldNotDeleteAnotherUsersComment() {
        when(commentRepository.findByIdAndCreatorUserIdAndIsDeletedFalse(5L, 99L))
                .thenReturn(Optional.empty());

        boolean deleted = commentService.deleteCommentByOwner(5L, 99L);

        assertFalse(deleted);
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
