package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {

    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
