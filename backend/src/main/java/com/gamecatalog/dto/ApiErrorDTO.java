package com.gamecatalog.dto;

// EN: Common error response body used instead of raw maps or plain strings.
// RU: Общий формат ошибки вместо сырых map или обычных строк.
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
