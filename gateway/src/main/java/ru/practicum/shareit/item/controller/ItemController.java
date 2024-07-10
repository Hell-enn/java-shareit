package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс-контроллер шлюза ItemController принимает HTTP-запросы,
 * касающиеся взаимодействия с арендуемыми вещами,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * ItemClient, где с помощью RestTemplate объект преобразуется в
 * HTTP-запрос, передаваемый в микросервис Server, где содержится основная
 * бизнес-логика по взаимодействию с объектами арендуемых вещей.
 */
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, публкующего информацию о своей вещи),
     * @param itemDto (объект вещи).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект опубликованной вещи
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody @NotNull ItemPostDto itemDto) {
        log.debug("Принят запрос на добавление вещи {} от пользователя с id = {}", itemDto.getName(), userId);
        return itemClient.postItem(userId, itemDto);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, обновляющего информацию о своей вещи),
     * @param itemDto (объект, содержащий обновленную информацию о вещи),
     * @param itemId (идентификатор вещи, информацию о которой необходимо обновить).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект обновленной вещи
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody @NotNull ItemPatchDto itemDto,
                                            @PathVariable Long itemId) {
        log.debug("Принят запрос на обновление вещи с id={} от пользователя с id = {}", itemId, userId);
        return itemClient.patchItem(userId, itemDto, itemId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на получение списка его вещей,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, получающего список своих вещей)
     * @param from (позиция первого объекта вещ в списке, с которого требуется
     *              вернуть обозначенное в size количество объектов)
     * @param size (количество объектов вещей, которое требуется вернуть)
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю список его вещей,
     * начиная с объекта в позиции from, в количестве size, или код ответа, отличный от 2**,
     * с описанием причины возникновения ошибки)
     */
    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero String from,
                                     @RequestParam(defaultValue = "20") @Positive String size) {
        log.debug("Принят запрос на получение списка всех вещей пользователя с id={}", userId);
        return itemClient.getItems(userId, Integer.parseInt(from), Integer.parseInt(size));
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, запрашивающего информацию о вещи),
     * @param itemId (идентификатор запрашиваемой вещи).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект вещи с идентификатором itemId
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable(name = "id") Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение вещи с id={}", itemId);
        return itemClient.getItem(userId, itemId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на получение списка вещей
     * по описанию, парсит его в понятные java, валидируемые объекты:
     * @param text (подстрока, которая впоследствии будет использоваться при поиске
     *             вещи по её наименованию(name) или описанию(description)),
     * @param userId (идентификатор пользователя, получающего список вещей по описанию),
     * @param from (позиция первого объекта вещи в списке, с которого требуется
     *             вернуть обозначенное в size количество объектов)
     * @param size (количество объектов вещей, которое требуется вернуть)
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю список вещей,
     * начиная с объекта в позиции from, в количестве size, или код ответа, отличный от 2**,
     * с описанием причины возникновения ошибки)
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
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект опубликованного комментария
     * к вещи или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody @NotNull CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на добавление комментария к вещи с id = {}", itemId);
        return itemClient.addComment(itemId, commentDto, userId);
    }
}
