package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Модель данных вещи для обновления, приходящая в теле HTTP-запроса в микросервис-шлюз.
 * Содержит поля:
 *  name - наименование вещи,
 *  description - описание вещи,
 *  available - флаг доступности вещи для бронирования,
 *  owner - идентификатор владельца вещи,
 *  requestId - идентификатор запроса, на основании которого добавлена вещь(опционально),
 *  comments - список комментариев арендуемой вещи, оставленные пользователями, бравшими вещь в аренду.
 */
@Data
@AllArgsConstructor
public class ItemPatchDto {
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<String> comments;
}
