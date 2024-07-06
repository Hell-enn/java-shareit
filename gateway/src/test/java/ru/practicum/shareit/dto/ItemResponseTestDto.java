package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponseTestDto {
    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}
