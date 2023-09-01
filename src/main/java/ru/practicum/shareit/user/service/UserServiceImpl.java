package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    final UserStorage userStorage;
    final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {

        log.info("Start to getting all users");

        List<User> users = userStorage.getAllUsers();
        if (users != null) {
            List<UserDto> usersDto = new ArrayList<>();
            UserDto userDto;
            for (User u : users) {
                userDto = userMapper.toUserDto(u);
                usersDto.add(userDto);
            }
            return usersDto;
        } else {
            throw new NotFoundException("Storage is empty");
        }
    }

    @Override
    public UserDto getUserById(int id) {

        log.info("Start to getting user with id {}", id);

        return userMapper.toUserDto(
                Optional.of(
                        userStorage.getUserById(id))
                        .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    public UserDto add(UserDto userDto) {

        log.info("Start to adding user with name {}", userDto.getName());

        User user = userMapper.toUser(userDto);

        return userMapper.toUserDto(
                Optional.of(
                        userStorage.add(user))
                        .orElseThrow(() ->
                                new ErrorException("In process of saving User error was acquired")));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {

        log.info("Start to updating user with id {}", id);

        User user = userMapper.toUser(getUserById(id));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(
                Optional.of(
                        userStorage.update(user))
                        .orElseThrow(() ->
                                new ErrorException("In process of updating User error was acquired")));
    }

    @Override
    public void delete(int id) {

        log.info("Start to deleting user with id {}", id);

        userStorage.delete(id);
    }
}
