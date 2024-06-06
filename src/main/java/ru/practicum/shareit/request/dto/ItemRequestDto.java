package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @Size(min = 1, max = 200)
    private final String description;
    private final Long requestor;
}
