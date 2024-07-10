package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Модель данных бронирования вещи, приходящая в теле HTTP-запроса в микросервис-шлюз.
 * Содержит поля:
 *  id - идентификатор объекта бронирования,
 *  start - момент начала срока аренды вещи,
 *  end - момент окончания срока аренды вещи,
 *  itemId - идентификатор объекта вещи бронирования,
 *  bookerId - идентификатор объекта пользователя, который отправил заявку на бронирование,
 *  status - состояние бронирования, передаваемое в виде строки.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    @NotNull
    private Long itemId;
    private Long bookerId;
    private String status;
}
