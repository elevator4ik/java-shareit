package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "1000") @Min(1) Integer size) {

        return userClient.getUsers(from, size);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {

        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated({Create.class})
                                          @RequestBody UserDto userDto) {

        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId,
                                             @Validated({Update.class})
                                             @RequestBody UserDto userDto) {

        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {

        userClient.deleteUser(userId);
    }
}