package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    /**
     * Метод добавляет новый запрос в список в случае,
     * если он в нём отсутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает добавленный объект.
     *
     * @param itemRequestDto ()
     * @return
     */
    ItemRequestDto postItemRequest(Long userId, ItemRequestDto itemRequestDto);


    /**
     * Метод обновляет объект запроса в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param itemRequestDto ()
     * @return
     */
    ItemRequestDto patchItemRequest(Long userId, ItemRequestDto itemRequestDto, Long requestId);


    /**
     * Метод возвращает список запросов из хранилища.
     *
     * @return
     */
    List<ItemRequestDto> getItemRequests();


    /**
     * Метод удаляет запрос с itemRequestId из хранилища
     */
    void deleteItemRequest(long itemRequestId);


    /**
     * Метод возвращает объект запроса по его id из хранилища.
     *
     * @param id
     * @return
     */
    ItemRequestDto getItemRequest(long id);
}
