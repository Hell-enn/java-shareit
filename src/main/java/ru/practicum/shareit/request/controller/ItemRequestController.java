package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestServiceImpl;

    /**
     * Эндпоинт. Метод добавляет новый запрос в список с
     * помощью соответствующего метода интерфеса хранилища -
     * ItemRequestService. В случае успеха возвращает добавленный объект.
     *
     * @param itemRequestDto ()
     * @return ItemRequestDto
     */
    @PostMapping
    public ItemRequestDto postItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Принят запрос на получение запроса вещи от пользователя с id={}", userId);
        return itemRequestServiceImpl.postItemRequest(userId, itemRequestDto);
    }


    /**
     * Эндпоинт. Метод обновляет объект запроса в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param itemRequestDto ()
     * @return ItemRequestDto
     */
    @PatchMapping
    public ItemRequestDto patchItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @RequestBody ItemRequestDto itemRequestDto,
                                           @PathVariable Long requestId) {
        log.debug("Принят запрос на обновление запроса на вещь с id={} от пользователя с id = {}", requestId, userId);
        return itemRequestServiceImpl.patchItemRequest(userId, itemRequestDto, requestId);
    }


    /**
     * Эндпоинт. Метод возвращает список запросов.
     *
     * @return
     */
    @GetMapping
    public List<ItemRequestDto> getItemRequests() {
        log.debug("Принят запрос на получение списка всех всех запросов на вещи");
        return itemRequestServiceImpl.getItemRequests();
    }


    /**
     * Эндпоинт. Удаляет запрос с itemRequestId
     */
    @DeleteMapping("/{id}")
    public void deleteItemRequest(@PathVariable(name = "id") Long itemRequestId) {
        log.debug("Принят запрос на удаление запроса на вещь с id={}", itemRequestId);
        itemRequestServiceImpl.deleteItemRequest(itemRequestId);
    }


    /**
     * Эндпоинт. Метод возвращает объект запроса по его id.
     *
     * @return
     */
    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable Long id) {
        log.debug("Принят запрос на получение запроса на вещь с id={}", id);
        return itemRequestServiceImpl.getItemRequest(id);
    }
}
