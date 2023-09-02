package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(),
                userDto.getEmail(),
                userDto.getName());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(),
                user.getEmail(),
                user.getName());
    }

    public List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        UserDto userDto;
        for (User u : users) {
            userDto = toUserDto(u);
            usersDto.add(userDto);
        }
        return usersDto;
    }
}
