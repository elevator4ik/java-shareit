package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class CommentDto {

    private Integer id;
    @NotEmpty(groups = {Create.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
