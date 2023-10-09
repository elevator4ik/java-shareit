package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {

    private Integer id;
    @NotEmpty(groups = {Create.class, Update.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
