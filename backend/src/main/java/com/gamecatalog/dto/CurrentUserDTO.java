package com.gamecatalog.dto;

// EN: Public user shape returned to the frontend after JWT authentication.
// RU: Публичная форма пользователя, которую frontend получает после JWT-авторизации.
public class CurrentUserDTO {
    private Long id;
    private String username;
    private String role;

    public CurrentUserDTO(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
