package com.gamecatalog.controller;

import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Returns product details together with its comments.
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Uses the external IGDB id from the frontend and returns a flat response.
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductWithComments(@PathVariable Long id) {
        Product product = productService.getProductByIgdbId(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        List<Comment> comments = productService.getCommentsByProductId(product.getId());

        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", product.getId());
        productMap.put("title", product.getTitle());
        productMap.put("description", product.getDescription());
        productMap.put("imageUrl", product.getImageUrl());
        productMap.put("creationDate", product.getCreationDate());
        productMap.put("isDeleted", product.getIsDeleted());
        productMap.put("creatorUserId", product.getCreatorUserId());
        
        List<Map<String, Object>> commentMaps = comments.stream().map(comment -> {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("description", comment.getDescription());
            commentMap.put("creationDate", comment.getCreationDate());
            commentMap.put("isDeleted", comment.getIsDeleted());
            commentMap.put("creatorUserId", comment.getCreatorUserId());
            return commentMap;
        }).collect(Collectors.toList());
        
        response.put("product", productMap);
        response.put("comments", commentMaps);

        return ResponseEntity.ok(response);
    }
}
