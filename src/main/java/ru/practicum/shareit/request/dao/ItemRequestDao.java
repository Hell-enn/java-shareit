package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestDao {
    /**
     * Добавляет объект itemRequest в список.
     * @param itemRequest
     */
    ItemRequest addItemRequest(Long userId, ItemRequestDto itemRequest);


    /**
     * Обновляет объект itemRequest в списке.
     * @param itemRequest
     */
    ItemRequest updateItemRequest(Long userId, ItemRequestDto itemRequest, Long requestId);


    /**
     * Удаляет объект типа ItemRequest с id из списка.
     * @param id
     */
    void deleteItemRequest(long id);


    /**
     * Метод возвращает из хранилища объект запроса по его id.
     * @param id
     * @return
     */
    ItemRequest getItemRequest(long id);


    /**
     * Метод отвечает на вопрос, содержится ли запрос с
     * данным id в списке.
     * @return
     */
    boolean containsItemRequest(long id);


    /**
     * Метод возвращает список запросов.
     * @return
     */
    List<ItemRequest> getItemRequests();
}
