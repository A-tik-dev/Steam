package com.gamecatalog.repository;

import com.gamecatalog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Database access for comments.
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductIdAndIsDeletedFalse(Long productId);
    List<Comment> findByIsDeletedFalse();
    java.util.Optional<Comment> findByIdAndCreatorUserIdAndIsDeletedFalse(Long id, Long creatorUserId);
}
