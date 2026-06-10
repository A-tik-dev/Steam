package com.gamecatalog.mapper;

import com.gamecatalog.dto.CurrentUserDTO;
import com.gamecatalog.entity.User;
import org.springframework.stereotype.Component;

// EN: Converts User entities into small public DTOs without password data.
// RU: Преобразует User entity в маленькие публичные DTO без данных пароля.
@Component
public class UserMapper {
    public CurrentUserDTO toCurrentUserDTO(User user) {
        return new CurrentUserDTO(user.getId(), user.getUsername(), user.getRole());
    }
}
