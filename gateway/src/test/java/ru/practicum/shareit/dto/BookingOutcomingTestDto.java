package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingOutcomingTestDto {
    private Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemDto item;
    private final UserDto booker;
    private String status;
}
