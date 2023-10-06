package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestDto {

    private Integer id;
    @NotNull(groups = {Create.class})
    private String description;
    private List<RequestedItem> items;
    private LocalDateTime created;

    @Setter
    @Getter
    @ToString
    @AllArgsConstructor
    public static class RequestedItem {
        private Integer id;
        private String name;
        private String description;
        private Boolean available;
        private Integer requestId;
    }
}
