package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;

/**
 * Модель данных бронирования вещи, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
public class BookingOutcomingDto {
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    @NotNull
    private final ItemDto item;
    @NotNull
    private final UserDto booker;
    private String status;
}
