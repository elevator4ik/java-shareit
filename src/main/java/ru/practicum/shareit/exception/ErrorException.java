package ru.practicum.shareit.exception;

public class ErrorException extends RuntimeException {

    public ErrorException(String massage) {
        super(massage);
    }
}
