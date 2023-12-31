package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(PageRequest pageRequest) {

        return userRepository.findAll(pageRequest)
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " not found"));
    }
}
