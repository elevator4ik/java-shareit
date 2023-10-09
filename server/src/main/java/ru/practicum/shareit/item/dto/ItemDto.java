package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class ItemDto implements Comparable<ItemDto> {

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
    @ToString
    public static class ItemBookingDto {
        Integer id;
        LocalDateTime start;
        LocalDateTime end;
        Integer bookerId;
    }

    @Override
    public int compareTo(ItemDto o) {
        return this.getId().compareTo(o.getId());
    }
}
