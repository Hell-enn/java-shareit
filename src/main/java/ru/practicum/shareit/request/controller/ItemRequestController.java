package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestServiceImpl;

    /**
     * Эндпоинт. Метод добавляет новый запрос в список с
     * помощью соответствующего метода интерфеса хранилища -
     * ItemRequestService. В случае успеха возвращает добавленный объект.
     *
     * @param itemRequest
     * @return
     */
    @PostMapping
    public ItemRequestDto postItemRequest(@Valid @RequestBody ItemRequest itemRequest) {
        return itemRequestServiceImpl.postItemRequest(itemRequest);
    }


    /**
     * Эндпоинт. Метод обновляет объект запроса в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param itemRequest
     * @return
     */
    @PatchMapping
    public ItemRequestDto patchItemRequest(@Valid @RequestBody ItemRequest itemRequest) {
        return itemRequestServiceImpl.patchItemRequest(itemRequest);
    }


    /**
     * Эндпоинт. Метод возвращает список запросов.
     *
     * @return
     */
    @GetMapping
    public List<ItemRequestDto> getItemRequests() {
        return itemRequestServiceImpl.getItemRequests();
    }


    /**
     * Эндпоинт. Удаляет запрос с itemRequestId
     */
    @DeleteMapping("/{id}")
    public void deleteItemRequest(@PathVariable(name = "id") long itemRequestId) {
        itemRequestServiceImpl.deleteItemRequest(itemRequestId);
    }


    /**
     * Эндпоинт. Метод возвращает объект запроса по его id.
     *
     * @return
     */
    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable long id) {
        return itemRequestServiceImpl.getItemRequest(id);
    }
}
