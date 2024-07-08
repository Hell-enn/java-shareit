package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ItemRequestInDto {
    private Long id;
    private String description;
    private Long requestor;
}
