package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * Класс-контроллер ItemController принимает HTTP-запросы,
 * касающиеся взаимодействия с арендуемыми вещами,
 * преобразует их в объекты Java и маршрутизирует в сервисный слой
 * ItemService для последующего взаимодействия с объектам-вещами
 * из хранилища ItemDao.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemServiceImpl;
    
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
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody ItemDto itemDto) {
        return itemServiceImpl.postItem(userId, itemDto);
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
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        return itemServiceImpl.patchItem(userId, itemDto, itemId);
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
     * @return List<ItemDto>
     */
    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemServiceImpl.getItems(userId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на удаление
     * объекта типа Item и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * удаления объекта типа Item из хранилища.
     *
     * @param itemId (объект вещи, которую необходимо удалить из хранилища)
     */
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable(name = "id") Long itemId) {
        itemServiceImpl.deleteItem(itemId);
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
     * @param itemId (объект арендуемой вещи, который необходимо удалить из хранилища)
     *
     * @return ItemDto
     */
    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable(name = "id") Long itemId) {
        return itemServiceImpl.getItem(itemId);
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
     * @return List<ItemDto>
     */
    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        return itemServiceImpl.getItemsBySearch(text);
    }
}
