package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Класс-исключение, объекты которого выбрасываются в случае, если
 * в строке запроса были переданы некорректные параметры.
 */
@Getter
public class UnsupportedOperationException extends RuntimeException {
    private final String state;

    public UnsupportedOperationException(String message, String state) {
        super(message);
        this.state = state;
    }
}
