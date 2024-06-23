package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
    /**
     * Метод добавляет новый запрос в список в случае,
     * если он в нём отсутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает добавленный объект.
     *
     * @param itemRequestDto (объект запроса, который необходимо добавить)
     * @param userId (идентификатор пользователя, который отправил запрос на добавление)
     *
     * @return ItemRequestOutDto
     */
    ItemRequestOutDto postItemRequest(Long userId, ItemRequestInDto itemRequestDto);


    /**
     * Метод обновляет объект запроса в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param itemRequestDto (объект запроса с полями, которые необходимо обновить в уже существующем объекте)
     * @param userId (идентификатор пользователя, отправившего запрос на обновление объекта в базе)
     * @param requestId (идентификатор запроса, который необходимо обновить в базе)
     *
     * @return ItemRequestOutDto
     */
    ItemRequestOutDto patchItemRequest(Long userId, ItemRequestInDto itemRequestDto, Long requestId);


    /**
     * Метод возвращает список запросов пользователя с идентификатором userId из хранилища.
     *
     * @param userId (идентификатор пользователя, чьи запросы необходимо вернуть)
     *
     * @return List<ItemRequestOutDto>
     */
    List<ItemRequestOutDto> getItemRequests(Long userId);


    /**
     * Метод возвращает объект запроса по его идентификатору id из хранилища.
     *
     * @param id (идентификатор запроса к вещи)
     *
     * @return ItemRequestOutDto
     */
    ItemRequestOutDto getItemRequest(Long id, Long userId);


    /**
     * Метод возвращает список запросов к вещам с позиции from в хранилище
     * в количестве size в порядке от более новых к более старым.
     *
     * @param from (Порядковый номер элемента в соответствующей таблице в базе данных)
     * @param size (Количество запросов, которое необходимо передать за один вызов метода)
     *
     * @return List<ItemRequestOutDto> (список запросов в количестве size)
     */
    List<ItemRequestOutDto> getAllItemRequests(Long userId, Integer from, Integer size);
}
