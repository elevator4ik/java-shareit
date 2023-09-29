package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

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
