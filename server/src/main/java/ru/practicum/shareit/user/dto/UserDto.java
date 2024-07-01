package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * Модель данных пользователя, возвращаемая в теле ответа на HTTP-запрос.
 */
@Data
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    @Size(min = 1)
    private final String name;
    @Size(min = 1)
    @Email(message = "Неверный формат электронной почты!")
    private final String email;
}
