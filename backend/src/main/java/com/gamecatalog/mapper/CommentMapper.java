package com.gamecatalog.mapper;

import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.dto.ProfileCommentDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.UserRepository;
import org.springframework.stereotype.Component;

// EN: Converts Comment entities into DTOs returned by product details and profile endpoints.
// RU: Преобразует Comment entity в DTO для деталей продукта и профиля пользователя.
@Component
public class CommentMapper {
    private final UserRepository userRepository;

    public CommentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // EN: DTO for the game details screen; includes author username for display.
    // RU: DTO для экрана деталей игры; включает username автора для отображения.
    public CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setDescription(comment.getDescription());
        dto.setCreationDate(comment.getCreationDate());
        dto.setIsDeleted(comment.getIsDeleted());
        dto.setCreatorUserId(comment.getCreatorUserId());
        dto.setCreatorUsername(resolveUsername(comment.getCreatorUserId()));
        return dto;
    }

    // EN: DTO for profile history; includes the game that received the comment.
    // RU: DTO для истории в профиле; включает игру, к которой написан комментарий.
    public ProfileCommentDTO toProfileDTO(Comment comment) {
        ProfileCommentDTO dto = new ProfileCommentDTO();
        dto.setCommentId(comment.getId());
        dto.setDescription(comment.getDescription());
        dto.setCreationDate(comment.getCreationDate());

        if (comment.getProduct() != null) {
            dto.setProductId(comment.getProduct().getId());
            dto.setProductTitle(comment.getProduct().getTitle());
            dto.setProductImageUrl(comment.getProduct().getImageUrl());
        }

        return dto;
    }

    private String resolveUsername(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("System");
    }
}
