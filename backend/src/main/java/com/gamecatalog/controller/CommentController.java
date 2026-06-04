package com.gamecatalog.controller;

import com.gamecatalog.dto.CommentCreateDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Handles comment read and create operations for products.
@RestController
@RequestMapping("/api/products")
public class CommentController {

    private final ProductService productService;

    public CommentController(ProductService productService) {
        this.productService = productService;
    }

    // Returns active comments for a product.
    @GetMapping("/{productId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByProductId(@PathVariable Long productId) {
        List<Comment> comments = productService.getCommentsByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    // Creates a new comment from the request payload.
    @PostMapping("/{productId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long productId,
            @RequestBody CommentCreateDTO payload) {

        String description = payload.getDescription();
        Integer creatorUserId = payload.getCreatorUserId();

        Comment comment = productService.createComment(
            productId,
            description,
            creatorUserId != null ? creatorUserId.longValue() : null
        );

        return ResponseEntity.ok(comment);
    }
}
