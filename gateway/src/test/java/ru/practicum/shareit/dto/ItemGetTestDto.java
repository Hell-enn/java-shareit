package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemGetTestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long booker;
    private Long request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}