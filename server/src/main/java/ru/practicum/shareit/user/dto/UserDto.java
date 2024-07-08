package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Модель данных пользователя, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private final String name;
    private final String email;
}
