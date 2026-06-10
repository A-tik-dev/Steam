package com.gamecatalog.controller;

import com.gamecatalog.dto.CommentCreateDTO;
import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.service.CommentService;
import com.gamecatalog.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// EN: Handles comment read, create, and delete operations for products.
// RU: Обрабатывает чтение, создание и удаление комментариев к продуктам.
@RestController
@RequestMapping("/api/products")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    public CommentController(CommentService commentService, CurrentUserService currentUserService) {
        this.commentService = commentService;
        this.currentUserService = currentUserService;
    }

    // EN: Returns active comments (with usernames) for a product.
    // RU: Возвращает активные комментарии к продукту вместе с username авторов.
    @GetMapping("/{productId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
    }

    // EN: Creates a new comment; the creator is taken from the JWT token - authentication required.
    // RU: Создаёт новый комментарий; автор берётся из JWT-токена, поэтому нужна авторизация.
    @PostMapping("/{productId}/comments")
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long productId,
            @RequestBody CommentCreateDTO payload) {

        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        CommentDTO comment = commentService.createComment(
            productId,
            payload.getDescription(),
            user.getId()
        );

        return ResponseEntity.ok(comment);
    }

    // EN: Soft-deletes a comment; only the owner (identified by JWT) can delete their own comment.
    // RU: Мягко удаляет комментарий; удалить может только владелец, определённый по JWT.
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        User user = currentUserService.getCurrentUser().orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        boolean deleted = commentService.deleteCommentByOwner(commentId, user.getId());
        if (!deleted) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.noContent().build();
    }
}
