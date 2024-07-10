package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Модель данных комментария к вещи, приходящая в теле HTTP-запроса в микросервис-шлюз.
 * Содержит поля:
 *  id - идентификатор объекта комментария,
 *  text - содержание комментария,
 *  author - идентификатор автора комментария,
 *  item - идентификатор вещи, к которой добавлен комментарий,
 *  authorName - имя автора комментария,
 *  created - момент публикации комментария к вещи.
 */
@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    @NotNull
    private Long author;
    @NotNull
    private Long item;
    private String authorName;
    private LocalDateTime created;
}
