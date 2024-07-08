package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Модель данных запроса вещи для публикации, приходящая в теле HTTP-запроса в микросервис-шлюз.
 * Содержит поля:
 *  id - идентификатор объекта запроса вещи,
 *  description - описание запроса вещи,
 *  requestor - идентификатор объекта пользователя, который отправил запрос вещи.
 */
@Data
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestPostDto {
    private Long id;
    @Size(min = 1, max = 200)
    @NotNull
    @NotBlank
    private String description;
    private Long requestor;
}
