package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorException extends RuntimeException {

    public ErrorException(String massage) {
        super(massage);
    }
}
