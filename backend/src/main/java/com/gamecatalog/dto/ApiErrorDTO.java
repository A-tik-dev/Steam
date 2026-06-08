package com.gamecatalog.dto;

public class ApiErrorDTO {
    private String message;

    public ApiErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
