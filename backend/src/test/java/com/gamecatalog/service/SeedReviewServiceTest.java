package com.gamecatalog.service;

import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeedReviewServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MockReviewerService mockReviewerService;

    private SeedReviewService seedReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seedReviewService = new SeedReviewService(commentRepository, mockReviewerService);
    }

    @Test
    void shouldCreateSeededReviewsWithMockReviewersInsteadOfSystemUser() {
        Product product = new Product("Test Game", "Test summary", 1L, null);
        product.setId(55L);

        User reviewer = new User("reviewer", "password", "ROLE_REVIEWER");
        reviewer.setId(10L);
        when(mockReviewerService.getOrCreateReviewers()).thenReturn(List.of(reviewer));

        seedReviewService.createReviews(product, 90.0);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<Comment>> commentsCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(commentRepository).saveAll(commentsCaptor.capture());

        List<Comment> comments = new ArrayList<>();
        commentsCaptor.getValue().forEach(comments::add);

        assertFalse(comments.isEmpty());
        assertTrue(comments.stream().noneMatch(comment -> Long.valueOf(1L).equals(comment.getCreatorUserId())));
    }
}
