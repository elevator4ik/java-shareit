package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class UserDto {

    private int id;
    @Email
    @NotNull(groups = {Create.class})
    private String email;
    @NotNull(groups = {Create.class})
    private String name;
}
