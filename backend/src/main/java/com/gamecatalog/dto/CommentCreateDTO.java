package com.gamecatalog.dto;

// EN: Request body for creating a new product comment.
// RU: Тело запроса для создания нового комментария к продукту.
public class CommentCreateDTO {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
