package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Модель данных вещи, используемая на уровне хранилища.
 */
@Data
@AllArgsConstructor
public class Item {
    private long id;
    @NotNull(message = "Поле name отсутствует!")
    @Size(min = 1)
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull(message = "Поле available отсутствует!")
    private Boolean available;
    private final User owner;
    private final ItemRequest request;
}