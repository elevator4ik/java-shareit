package ru.practicum.shareit.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(PageRequest pageRequest);

    UserDto getUserById(int id);

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, int id);

    void delete(int id);
}