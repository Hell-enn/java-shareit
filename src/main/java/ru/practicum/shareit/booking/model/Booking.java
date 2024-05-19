package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Модель данных бронирования, используемая на уровне хранилища.
 */
@Data
@AllArgsConstructor
public class Booking {
    private long id;
    @NotNull(message = "Поле start отсутствует!")
    private final LocalDateTime start;
    @NotNull(message = "Поле end отсутствует!")
    private final LocalDateTime end;
    @NotNull(message = "Поле item отсутствует!")
    private final Item item;
    @NotNull(message = "Поле booker отсутствует!")
    private final User booker;
    private String status;
}
