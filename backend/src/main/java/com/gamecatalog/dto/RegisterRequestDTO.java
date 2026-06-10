package com.gamecatalog.dto;

// EN: Registration request body sent from the create-account form.
// RU: Тело запроса регистрации, которое отправляет форма создания аккаунта.
public class RegisterRequestDTO {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
