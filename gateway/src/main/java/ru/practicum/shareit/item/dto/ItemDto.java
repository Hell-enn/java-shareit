package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Модель данных вещи, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @Size(min = 1)
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<String> comments;
}