package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * Модель данных вещи, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @Size(min = 1)
    private final String name;
    @Size(min = 1, max = 200)
    private final String description;
    private Boolean available;
    private final Long owner;
    private Long request;
}