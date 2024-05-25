package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Модель данных пользователя, используемая на уровне хранилища.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
