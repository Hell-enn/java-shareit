package ru.practicum.shareit.exception;

/**
 * Класс-исключение, объекты которого выбрасываются в случае, если
 * подобная манипуляция с объектом запрещена.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}