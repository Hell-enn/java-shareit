package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * Класс-контроллер сервера RequestServerController принимает отфильтрованные
 * HTTP-запросы из шлюза, касающиеся взаимодействия с запросами вещей,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * ItemRequestService, где содержится основная бизнес-логика по взаимодействию с объектами запросов вещей.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestServerController {

    private final ItemRequestService itemRequestServiceImpl;

    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на добавление
     * объекта типа ItemRequest и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * добавления объекта типа ItemRequest в хранилище.
     *
     * @param userId (идентификатор пользователя, отправившего запрос на добавление запроса вещи),
     * @param itemRequestDto (объект запроса вещи(dto), который необходимо добавить в хранилище).
     *
     * @return ItemRequestOutDto(объект опубликованного запроса вещи)
     */
    @PostMapping
    public ItemRequestOutDto postItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemRequestInDto itemRequestDto) {
        log.debug("Принят запрос на добавление запроса вещи от пользователя с id={}", userId);
        return itemRequestServiceImpl.postItemRequest(userId, itemRequestDto);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на обновление
     * объекта типа ItemRequest и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * обновления объекта типа ItemRequest в хранилище.
     *
     * @param userId (объект пользователя, который отправил запрос на обновление запроса вещи в хранилище),
     * @param itemRequestInDto (объект запроса вещи(dto), который необходимо обновить в хранилище),
     * @param requestId (идентификатор запроса вещи, который необходимо обновить в хранилище).
     *
     * @return ItemRequestOutDto(объект обновленного запроса вещи)
     */
    @PatchMapping("/{requestId}")
    public ItemRequestOutDto patchItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemRequestInDto itemRequestInDto,
                                              @PathVariable Long requestId) {
        log.debug("Принят запрос на обновление запроса на вещь с id={} от пользователя с id = {}", requestId, userId);
        return itemRequestServiceImpl.patchItemRequest(userId, itemRequestInDto, requestId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на получение
     * всех объектов типа ItemRequest пользователя с идентификатором userId
     * и направляет его в текущий эндпоинт.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объектов типа ItemRequest из хранилища.
     *
     * @param userId (идентификатор пользователя, список запросов которого необходимо извлечь).
     *
     * @return List<ItemRequestOutDto>(список объектов запросов вещей пользователя с userId)
     */
    @GetMapping
    public List<ItemRequestOutDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение списка всех запросов на вещи пользователя с id={}", userId);
        return itemRequestServiceImpl.getItemRequests(userId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на получение
     * объекта типа ItemRequest и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объекта типа ItemRequest из хранилища.
     *
     * @param id (идентификатор запроса вещи, который необходимо получить из хранилища),
     * @param userId (идентификатор пользователя, запрашивающего информацию о запросе вещи).
     *
     * @return ItemRequestOutDto(объект запроса вещи с идентификатором id)
     */
    @GetMapping("/{id}")
    public ItemRequestOutDto getItemRequest(@PathVariable Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение запроса на вещь с id={} пользователем с id={}", id, userId);
        return itemRequestServiceImpl.getItemRequest(id, userId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на получение
     * всех объектов, начиная с элемента с порядковым номером from
     * в количестве size, типа ItemRequest и направляет его в текущий эндпоинт.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объектов типа ItemRequest из хранилища.
     *
     * @param from (порядковый номер объекта типа ItemRequest, начина с которого возвращаются объекты),
     * @param size (количество возвращаемых объектов),
     * @param userId (идентификатор пользователя, список запросов которого необходимо извлечь).
     *
     * @return List<ItemRequestOutDto>(список всех объектов запросов вещей, начиная с позиции в списке from,
     *                                 в количестве size)
     */
    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllItemRequests(@RequestParam Integer from,
                                                      @RequestParam Integer size,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение всех запросов в количестве {} с позиции {}", size, from);
        return itemRequestServiceImpl.getAllItemRequests(userId, from, size);
    }
}
