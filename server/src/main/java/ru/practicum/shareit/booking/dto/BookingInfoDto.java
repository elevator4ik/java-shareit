package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingInfoDto {

    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
}