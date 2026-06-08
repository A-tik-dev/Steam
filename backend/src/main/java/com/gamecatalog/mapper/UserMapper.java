package com.gamecatalog.mapper;

import com.gamecatalog.dto.CurrentUserDTO;
import com.gamecatalog.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public CurrentUserDTO toCurrentUserDTO(User user) {
        return new CurrentUserDTO(user.getId(), user.getUsername(), user.getRole());
    }
}
