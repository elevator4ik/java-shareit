package ru.practicum.shareit.exeption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BadRequestException extends RuntimeException {

    public BadRequestException(String massage) {
        super(massage);
    }
}
