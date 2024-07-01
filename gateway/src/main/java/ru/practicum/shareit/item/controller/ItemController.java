package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс-контроллер ItemController принимает HTTP-запросы,
 * касающиеся взаимодействия с арендуемыми вещами,
 * преобразует их в объекты Java и маршрутизирует в сервисный слой
 * ItemService для последующего взаимодействия с объектам-вещами
 * из хранилища ItemDao.
 */
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на добавление
     * объекта типа Item и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * добавления объекта типа Item в хранилище.
     *
     * @param userId (идентификатор пользователя, отправившего запрос на добавление вещи)
     * @param itemDto (объект арендуемой вещи(dto), который необходимо добавить в хранилище)
     *
     * @return ItemDto
     */
    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        log.debug("Принят запрос на добавление вещи {} от пользователя с id = {}", itemDto.getName(), userId);
        return itemClient.postItem(userId, itemDto);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на обновление
     * объекта типа Item и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * обновления объекта типа Item в хранилище.
     *
     * @param userId (объект пользователя, который отправил запрос на обновление вещи в хранилище)
     * @param itemDto (объект арендуемой вещи(dto), которую необходимо добавить в хранилище)
     * @param itemId (идентификатор арендуемой вещи, которую необходимо добавить в хранилище)
     *
     * @return ItemDto
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        log.debug("Принят запрос на обновление вещи с id={} от пользователя с id = {}", itemId, userId);
        return itemClient.patchItem(userId, itemDto, itemId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на получение
     * всех объектов типа Item пользователя с идентификатором userId
     * и направляет его в текущий эндпоинт.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объектов типа Item из хранилища.
     *
     * @param userId (идентификатор пользователя, список вещей которого необходимо извлечь)
     *
     * @return List<ItemGetDto>
     */
    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero String from,
                                     @RequestParam(defaultValue = "20") @Positive String size) {
        log.debug("Принят запрос на получение списка всех вещей пользователя с id={}", userId);
        return itemClient.getItems(userId, Integer.parseInt(from), Integer.parseInt(size));
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на получение
     * объекта типа Item и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объекта типа Item из хранилища.
     *
     * @param itemId (идентификатор арендуемой вещи, которую необходимо удалить из хранилища)
     * @param userId (идентификатор пользователя, отправившего запрос на удаление вещи)
     *
     * @return ItemGetDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable(name = "id") Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение вещи с id={}", itemId);
        return itemClient.getItem(userId, itemId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на получение
     * всех объектов типа Item, содержащих в названии или описании
     * переданную в параметре запроса подстроку,
     * и направляет его в текущий эндпоинт.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объектов типа Item из хранилища.
     *
     * @param text (подстрока, содержащая информацию о необходимой арендуемой вещи)
     *
     * @return List<ItemGetDto>
     */
    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.debug("Принят запрос на получение списка вещей, удовлетворяющих запросу '{}'", text);
        return itemClient.getItemsBySearch(text, userId, from, size);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на добавление
     * объекта типа CommentDto и направляет его в текущий эндпоинт.
     * Далее происходит маршрутизация в сервисный слой с бизнес-логикой.
     * Параметры:
     * @param itemId (идентификатор вещи, к которой добавляется комментарий)
     * @param commentDto (объект комментария)
     * @param userId (идентификатор пользователя, отправившего запрос на добавление комментария)
     *
     * @return CommentDto
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на добавление комментария к вещи с id = {}", itemId);
        return itemClient.addComment(itemId, commentDto, userId);
    }
}
