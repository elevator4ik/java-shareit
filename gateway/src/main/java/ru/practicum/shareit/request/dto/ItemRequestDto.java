package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Integer id;
    private String description;
    private List<RequestedItem> items;
    private LocalDateTime created;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class RequestedItem {
        private Integer id;
        private String name;
        private String description;
        private Boolean available;
    }
}
