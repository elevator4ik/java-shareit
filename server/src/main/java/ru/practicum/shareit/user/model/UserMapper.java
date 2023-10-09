package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public User toUser(UserDto userDto) {
        return new User(null,
                userDto.getEmail(),
                userDto.getName());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(),
                user.getEmail(),
                user.getName());
    }
}
