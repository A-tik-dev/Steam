package com.gamecatalog.dto;

import java.util.List;

public class UserProfileDTO {
    private CurrentUserDTO user;
    private List<ProfileCommentDTO> comments;
    private List<ProductDTO> favoriteGames;

    public CurrentUserDTO getUser() {
        return user;
    }

    public void setUser(CurrentUserDTO user) {
        this.user = user;
    }

    public List<ProfileCommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<ProfileCommentDTO> comments) {
        this.comments = comments;
    }

    public List<ProductDTO> getFavoriteGames() {
        return favoriteGames;
    }

    public void setFavoriteGames(List<ProductDTO> favoriteGames) {
        this.favoriteGames = favoriteGames;
    }
}
