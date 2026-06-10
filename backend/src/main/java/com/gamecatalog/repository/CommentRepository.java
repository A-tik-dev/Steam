package com.gamecatalog.repository;

import com.gamecatalog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// EN: Database access for comments, including owner checks and profile history.
// RU: Доступ к базе для комментариев, включая проверку владельца и историю профиля.
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductIdAndIsDeletedFalse(Long productId);
    List<Comment> findByCreatorUserIdAndIsDeletedFalseOrderByCreationDateDesc(Long creatorUserId);
    List<Comment> findByIsDeletedFalse();
    java.util.Optional<Comment> findByIdAndCreatorUserIdAndIsDeletedFalse(Long id, Long creatorUserId);
}
