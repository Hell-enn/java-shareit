package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Модель данных пользователя для публикации, приходящая в теле HTTP-запроса в микросервис-шлюз.
 * Содержит поля:
 *  id - идентификатор объекта пользователя,
 *  name - имя пользователя,
 *  email - адрес электронной почты пользователя.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Size(min = 1)
    private final String name;
    @Size(min = 1)
    @Email(message = "Неверный формат электронной почты!")
    @NotNull
    private final String email;
}
