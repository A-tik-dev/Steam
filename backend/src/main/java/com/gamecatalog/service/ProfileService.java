package com.gamecatalog.service;

import com.gamecatalog.dto.ProfileCommentDTO;
import com.gamecatalog.dto.UserProfileDTO;
import com.gamecatalog.entity.User;
import com.gamecatalog.mapper.CommentMapper;
import com.gamecatalog.mapper.UserMapper;
import com.gamecatalog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// EN: Aggregates profile data from user info, comments, and favorites.
// RU: Собирает данные профиля из информации о пользователе, комментариев и избранного.
@Service
public class ProfileService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final FavoriteService favoriteService;
    private final UserMapper userMapper;

    public ProfileService(CommentRepository commentRepository, CommentMapper commentMapper,
                          FavoriteService favoriteService, UserMapper userMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.favoriteService = favoriteService;
        this.userMapper = userMapper;
    }

    // EN: Builds the DTO returned by /api/profile/me.
    // RU: Собирает DTO, который возвращает /api/profile/me.
    public UserProfileDTO getProfile(User user) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUser(userMapper.toCurrentUserDTO(user));
        profile.setComments(getUserComments(user.getId()));
        profile.setFavoriteGames(favoriteService.getFavoriteProducts(user.getId()));
        return profile;
    }

    // EN: Loads the user's visible comments newest first.
    // RU: Загружает видимые комментарии пользователя, сначала новые.
    private List<ProfileCommentDTO> getUserComments(Long userId) {
        return commentRepository.findByCreatorUserIdAndIsDeletedFalseOrderByCreationDateDesc(userId)
                .stream()
                .map(commentMapper::toProfileDTO)
                .collect(Collectors.toList());
    }
}
