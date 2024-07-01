package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemGetDto {
    private Long id;
    @Size(min = 1)
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private Boolean available;
    private Long booker;
    private Long request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
