package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class ItemDto {

    private Integer id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;
    @NotNull(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
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
}
