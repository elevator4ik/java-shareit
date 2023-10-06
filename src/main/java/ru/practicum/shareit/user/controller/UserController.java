package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Min;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(name = "size", defaultValue = "1000") @Min(1) Integer size) {
        final PageRequest pageRequest = new MyPageRequest(from / size, size, Sort.unsorted());
        return service.getUsers(pageRequest);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    @PostMapping
    public UserDto add(@Validated({Create.class})
                       @RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id,
                          @Validated({Update.class})
                          @RequestBody UserDto userDto) {
        return service.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}