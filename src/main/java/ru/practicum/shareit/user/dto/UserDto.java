package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * Модель данных пользователя, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private final String name;
    @Email(message = "Неверный формат электронной почты!")
    private final String email;
}
