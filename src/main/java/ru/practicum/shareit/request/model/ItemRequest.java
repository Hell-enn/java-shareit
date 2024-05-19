package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    private long id;
    @Size(min = 1, max = 200)
    private final String description;
    @NotNull(message = "Поле requestor отсутствует!")
    private final User requestor;
    @NotNull(message = "Поле created отсутствует!")
    private final LocalDateTime created;
}
