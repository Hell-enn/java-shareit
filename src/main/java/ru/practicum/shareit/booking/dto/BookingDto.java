package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * Модель данных бронирования вещи, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long item;
    private final Long booker;
    private BookingStatus bookingStatus;
}
