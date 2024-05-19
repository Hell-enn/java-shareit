package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Модель данных пользователя, используемая на уровне хранилища.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotNull(message = "Поле name отсутствует!")
    private String name;
    @NotNull(message = "Поле email отсутствует!")
    @Email(message = "Неверный формат электронной почты!")
    private String email;
}
