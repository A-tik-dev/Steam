package com.gamecatalog.service;

import com.gamecatalog.dto.CommentDTO;
import com.gamecatalog.entity.Comment;
import com.gamecatalog.entity.Product;
import com.gamecatalog.mapper.CommentMapper;
import com.gamecatalog.repository.CommentRepository;
import com.gamecatalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final CommentMapper commentMapper;
    private final SeedReviewService seedReviewService;

    public CommentService(CommentRepository commentRepository, ProductRepository productRepository,
                          CommentMapper commentMapper, SeedReviewService seedReviewService) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.commentMapper = commentMapper;
        this.seedReviewService = seedReviewService;
    }

    @Transactional
    public List<CommentDTO> getCommentsByProductId(Long productId) {
        seedReviewService.repairLegacyOwners(productId);
        return commentRepository.findByProductIdAndIsDeletedFalse(productId)
                .stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createComment(Long productId, String description, Long creatorUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Comment comment = new Comment(description, creatorUserId, product);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDTO(saved);
    }

    @Transactional
    public boolean deleteCommentByOwner(Long commentId, Long requestingUserId) {
        return commentRepository.findByIdAndCreatorUserIdAndIsDeletedFalse(commentId, requestingUserId)
                .map(comment -> {
                    comment.setIsDeleted(true);
                    commentRepository.save(comment);
                    return true;
                })
                .orElse(false);
    }
}
