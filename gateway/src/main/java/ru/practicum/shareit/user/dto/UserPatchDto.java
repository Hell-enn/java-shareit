package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * Модель данных пользователя для обновления, приходящая в теле HTTP-запроса в микросервис-шлюз.
 * Содержит поля:
 *  name - имя пользователя,
 *  email - адрес электронной почты пользователя.
 */
@Data
@AllArgsConstructor
public class UserPatchDto {
    private final String name;
    @Size(min = 1)
    @Email(message = "Неверный формат электронной почты!")
    private final String email;
}
