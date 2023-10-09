package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private int id;
    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    private String email;
    @NotNull(groups = {Create.class})
    private String name;
}
