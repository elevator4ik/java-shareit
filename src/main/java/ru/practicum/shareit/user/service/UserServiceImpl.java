package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

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
            return userMapper.toUserDtoList(users);
        } else {
            throw new NotFoundException("Storage is empty");
        }
    }

    @Override
    public UserDto getUserById(int id) {

        log.info("Start to getting user with id {}", id);

        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public UserDto add(UserDto userDto) {

        log.info("Start to adding user with name {}", userDto.getName());

        User user = userMapper.toUser(userDto);

        return userMapper.toUserDto(userStorage.add(user));
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

        return userMapper.toUserDto(userStorage.update(user));
    }

    @Override
    public void delete(int id) {

        log.info("Start to deleting user with id {}", id);

        userStorage.delete(id);
    }
}
