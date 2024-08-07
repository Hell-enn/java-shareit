package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Модель данных бронирования вещи, приходящая в теле HTTP-запроса.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private String status;
}
