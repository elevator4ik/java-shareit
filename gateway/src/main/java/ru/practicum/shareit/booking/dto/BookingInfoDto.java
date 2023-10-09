package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingInfoDto {

    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @NotNull(groups = {Create.class})
    @Future(groups = {Create.class})
    private LocalDateTime end;
    @NotNull(groups = {Create.class})
    private Integer itemId;
}