package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers() {

        log.info("Start to getting all users");

        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return userMapper.toUserDtoList(users);
        } else {
            throw new NotFoundException("Repository is empty");
        }
    }

    @Override
    public UserDto getUserById(int id) {

        log.info("Start to getting user with id {}", id);

        return userMapper.toUserDto(
                getUserFromRepo(id));
    }

    @Override
    public UserDto add(UserDto userDto) {

        log.info("Start to adding user with name {}", userDto.getName());

        User user = userMapper.toUser(userDto);

        return userMapper.toUserDto(userRepository.saveAndFlush(user));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {

        log.info("Start to updating user with id {}", id);

        User user = getUserFromRepo(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(userRepository.saveAndFlush(user));
    }

    @Override
    public void delete(int id) {

        log.info("Start to deleting user with id {}", id);

        userRepository.deleteById(id);
    }

    private User getUserFromRepo(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
