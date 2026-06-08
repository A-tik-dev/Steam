package com.gamecatalog.mapper;

import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.dto.ProfileCommentDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.User;
import com.gamecatalog.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private final UserRepository userRepository;

    public CommentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
