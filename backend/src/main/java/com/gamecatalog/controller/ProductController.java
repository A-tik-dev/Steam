package com.gamecatalog.controller;

import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.dto.ProductDTO;
import com.gamecatalog.dto.ProductWithCommentsDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<ProductWithCommentsDTO> getProductWithComments(@PathVariable Long id) {
        Product product = productService.getProductByIgdbId(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        List<Comment> comments = productService.getCommentsByProductId(product.getId());

        ProductWithCommentsDTO response = new ProductWithCommentsDTO();
        
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setTitle(product.getTitle());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setCreationDate(product.getCreationDate());
        productDTO.setIsDeleted(product.getIsDeleted());
        productDTO.setCreatorUserId(product.getCreatorUserId());
        
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setDescription(comment.getDescription());
            commentDTO.setCreationDate(comment.getCreationDate());
            commentDTO.setIsDeleted(comment.getIsDeleted());
            commentDTO.setCreatorUserId(comment.getCreatorUserId());
            return commentDTO;
        }).collect(Collectors.toList());
        
        response.setProduct(productDTO);
        response.setComments(commentDTOs);

        return ResponseEntity.ok(response);
    }
}
