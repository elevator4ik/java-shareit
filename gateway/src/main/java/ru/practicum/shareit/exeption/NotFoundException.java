package ru.practicum.shareit.exeption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotFoundException extends RuntimeException {

    public NotFoundException(String massage) {
        super(massage);
    }

}
