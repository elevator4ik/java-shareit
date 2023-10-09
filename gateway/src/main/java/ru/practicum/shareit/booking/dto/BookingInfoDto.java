package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.exeption.BadRequestException;

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

    public BookingInfoDto startEndCheck(BookingInfoDto dto) {
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().equals(dto.getStart())) {
            throw new BadRequestException("Start not before end");
        } else {
            return dto;
        }
    }
}