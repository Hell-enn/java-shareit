package ru.practicum.shareit.exception;

/**
 * Класс-исключение, объекты которого выбрасываются в случае, если
 * объект не был найден в хранилище.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}