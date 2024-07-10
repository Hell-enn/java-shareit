package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestPatchDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс-контроллер шлюза ItemRequestController принимает HTTP-запросы,
 * касающиеся взаимодействия с запросами вещей,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * RequestClient, где с помощью RestTemplate объект преобразуется в
 * HTTP-запрос, передаваемый в микросервис Server, где содержится основная
 * бизнес-логика по взаимодействию с объектами запросов вещей.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    /**
     * Эндпоинт. Метод получает запрос пользователя на добавление запроса вещи, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, добавляющего запрос вещи),
     * @param itemRequestDto (объект с информацией о запросе вещи).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект запроса вещи
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PostMapping
    public ResponseEntity<Object> postItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Valid @RequestBody @NotNull ItemRequestPostDto itemRequestDto) {
        log.debug("Принят запрос на добавление запроса вещи от пользователя с id={}", userId);
        return requestClient.postItemRequest(userId, itemRequestDto);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на обновление объекта запроса, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, вносящего изменения в объект своего запроса),
     * @param itemRequestInDto (объект, содержащий поля с обновленной информацией для уже
     *                         существующего объекта запроса вещи),
     * @param requestId (идентификатор запроса вещи, информацию о котором необходимо обновить).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект обновленного запроса
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PatchMapping("/{requestId}")
    public ResponseEntity<Object> patchItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @Valid @RequestBody @NotNull ItemRequestPatchDto itemRequestInDto,
                                                   @PathVariable Long requestId) {
        log.debug("Принят запрос на обновление запроса на вещь с id={} от пользователя с id = {}", requestId, userId);
        return requestClient.patchItemRequest(userId, itemRequestInDto, requestId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на получение списка его запросов вещей,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, получающего список своих запросов вещей),
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю список его запросов вещей,
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение списка всех запросов на вещи пользователя с id={}", userId);
        return requestClient.getItemRequests(userId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на получение запроса вещи с id,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, получающего запрос вещи),
     * @param id (идентификатор запроса вещи).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект запроса вещи с идентификатором id
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequest(@PathVariable Long id,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение запроса на вещь с id={} пользователем с id={}", id, userId);
        return requestClient.getItemRequest(userId, id);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на получене списка всех запросов вещей,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, получающего список запросов вещей),
     * @param from (позиция первого объекта запроса вещи в списке, с которого требуется
     *             вернуть обозначенное в size количество объектов)
     * @param size (количество объектов запросов вещей, которое требуется вернуть)
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю список его запросов вещей,
     * начиная с объекта в позиции from, в количестве size, или код ответа, отличный от 2**,
     * с описанием причины возникновения ошибки)
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "20") @Positive Integer size,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос на получение всех запросов в количестве {} с позиции {}", size, from);
        return requestClient.getAllItemRequests(userId, from, size);
    }
}