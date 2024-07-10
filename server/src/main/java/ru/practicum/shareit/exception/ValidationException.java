package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Класс ValidationException необходим для создания
 * соответствующих объектов исключения в приложении
 * и их последующего выбрасывания в ситуациях, когда
 * объекты-сущности не проходят валидацию по определенным
 * параметрам.
 */
@Getter
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}