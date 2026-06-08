package com.gamecatalog.controller;

import com.gamecatalog.dto.CommentCreateDTO;
import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.repository.UserRepository;
import com.gamecatalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Handles comment read, create, and delete operations for products.
@RestController
@RequestMapping("/api/products")
public class CommentController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public CommentController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    // Returns active comments (with usernames) for a product.
    @GetMapping("/{productId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(@PathVariable Long productId) {
        List<CommentDTO> comments = productService.getCommentsAsDTOByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    // Creates a new comment; the creator is taken from the JWT token - authentication required.
    @PostMapping("/{productId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long productId,
            @RequestBody CommentCreateDTO payload) {

        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        CommentDTO comment = productService.createComment(
            productId,
            payload.getDescription(),
            userId
        );

        return ResponseEntity.ok(comment);
    }

    // Soft-deletes a comment; only the owner (identified by JWT) can delete their own comment.
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        boolean deleted = productService.deleteCommentByOwner(commentId, userId);
        if (!deleted) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.noContent().build();
    }

    // Resolves the currently authenticated user's database ID from the security context.
    private Long getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String username = auth.getName();
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .map(user -> user.getId())
                .orElse(null);
    }
}
