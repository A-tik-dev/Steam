package com.gamecatalog.controller;

import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.dto.ProductWithCommentsDTO;
import com.gamecatalog.entity.Product;
import com.gamecatalog.mapper.ProductMapper;
import com.gamecatalog.service.CommentService;
import com.gamecatalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// EN: Returns locally saved product details together with its comments.
// RU: Возвращает детали локально сохранённого продукта вместе с комментариями.
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CommentService commentService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, CommentService commentService, ProductMapper productMapper) {
        this.productService = productService;
        this.commentService = commentService;
        this.productMapper = productMapper;
    }

    // EN: Uses the external IGDB id from the frontend and returns product + comments with usernames.
    // RU: Использует внешний IGDB id с фронтенда и возвращает продукт + комментарии с именами пользователей.
    @GetMapping("/{id}")
    public ResponseEntity<ProductWithCommentsDTO> getProductWithComments(@PathVariable Long id) {
        Product product = productService.getProductByIgdbId(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        List<CommentDTO> commentDTOs = commentService.getCommentsByProductId(product.getId());

        ProductWithCommentsDTO response = new ProductWithCommentsDTO();
        response.setProduct(productMapper.toDTO(product));
        response.setComments(commentDTOs);

        return ResponseEntity.ok(response);
    }
}
