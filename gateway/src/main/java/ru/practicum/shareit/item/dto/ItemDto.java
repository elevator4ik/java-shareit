package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private ItemBookingDto nextBooking;
    private ItemBookingDto lastBooking;
    private List<CommentDto> comments;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class ItemBookingDto {
        Integer id;
        LocalDateTime start;
        LocalDateTime end;
        Integer bookerId;
    }
}
