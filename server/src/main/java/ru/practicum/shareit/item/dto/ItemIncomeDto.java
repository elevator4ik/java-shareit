package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ItemIncomeDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
