package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @Size(min = 1, max = 200)
    private final String description;
    private final Long requestor;
    private final LocalDateTime created;
}
