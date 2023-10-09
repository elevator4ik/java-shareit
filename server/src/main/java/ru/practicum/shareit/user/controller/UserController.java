package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam("from") Integer from,
                                  @RequestParam("size") Integer size) {
        final PageRequest pageRequest = PageRequest.of(from / size, size, Sort.unsorted());
        return service.getUsers(pageRequest);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id,
                          @RequestBody UserDto userDto) {
        return service.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}