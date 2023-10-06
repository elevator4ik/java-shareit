package ru.practicum.shareit.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User service")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    UserRepository userRepository;
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;

    User user = new User(1, "user@mail.com", "name");

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    @Order(2)
    void getUser() {
        UserDto expected = new UserDto(1, "user@mail.com", "name");
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any()))
                .thenReturn(expected);

        UserDto result = userService.getUserById(1);

        assertEquals(result, expected);
    }

    @Test
    void getUsers() {
        UserDto expected = new UserDto(1, "user@mail.com", "name");
        PageRequest pageRequest = new MyPageRequest(0, 1000, Sort.by(
                Sort.Direction.DESC, "created"));

        when(userRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toUserDto(any()))
                .thenReturn(expected);

        List<UserDto> result = userService.getUsers(pageRequest);

        assertEquals(result, List.of(expected));
    }

    @Test
    void addUser() {
        UserDto expected = new UserDto(1, "user@mail.com", "name");
        when(userMapper.toUser(any()))
                .thenReturn(user);
        when(userRepository.saveAndFlush(any()))
                .thenReturn(user);
        when(userMapper.toUserDto(any()))
                .thenReturn(expected);

        UserDto result = userService.add(expected);

        assertEquals(result.toString(), expected.toString());
    }
}
