package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;

import java.util.List;

/**
 * Интерфейс-сервис ItemService определяет контракт,
 * включающий сигнатуры ряда методов, которые реализуют
 * расширяющие его классы слоя бизнес-логики
 * в части работы с арендуемыми вещами приложения ShareIt.
 */
public interface ItemService {

    /**
     * Включает валидацию объекта типа ItemDto перед
     * перенаправлением его в слой доступа к данным (метод добавления новой вещи),
     * а также последующее преобразование возвращаемого из хранилища объекта
     * типа Item в ItemDto.
     *
     * @param userId (идентификатор пользователя, отправившего запрос на добавление объекта вещи)
     * @param itemDto (объект арендуемой вещи, содержащий информацию о ней,
     *                которую необходимо занести в хранилище)
     *
     * @return ItemDto
     */
    ItemDto postItem(Long userId, ItemDto itemDto);


    /**
     * Включает перенаправление объекта типа ItemDto
     * в слой доступа к данным (метод обновления уже существующей вещи),
     * а также последующее преобразование
     * возвращаемого из хранилища объекта типа Item в ItemDto.
     *
     * @param userId (идентификатор объекта типа User, вещь которого нужно обновить
     *                в хранилище)
     * @param itemDto (объект, содержащий информацию о вещи,
     *                которую необходимо обновить в хранилище)
     * @param itemId (идентификатор объекта типа Item, который нужно обновить
     *                в хранилище)
     *
     * @return ItemDto
     */
    ItemDto patchItem(Long userId, ItemDto itemDto, Long itemId);


    /**
     * Включает маршрутизацию в слой доступа к данным
     * (метод извлечения из хранилища всех арендуемых вещей конкретного пользователя),
     * а также последующее преобразование полученного списка
     * объектов типа Item в список объектов типа ItemDto.
     *
     * @param userId (идентификатор пользователя, информацию о чьих арендуемых
     *               вещах необходимо извлечь из хранилища)
     *
     * @return List<ItemDto>
     */
    List<ItemGetDto> getItems(Long userId, Integer from, Integer size);


    /**
     * Включает маршрутизацию в слой доступа к данным
     * (метод получения из хранилища объекта типа Item с идентификатором itemId),
     * а также последующее преобразование этого объекта в тип ItemDto.
     *
     * @param itemId (идентификатор объекта типа Item, который нужно извлечь
     *                из хранилища)
     *
     * @return ItemDto
     */
    ItemGetDto getItem(Long itemId, Long userId);


    /**
     * Включает маршрутизацию в слой доступа к данным
     * (метод извлечения из хранилища всех арендуемых вещей по результатам поиска),
     * а также последующее преобразование полученного списка
     * объектов типа Item в список объектов типа ItemDto.
     *
     * @param text (подстрока, используемая впоследствии для поиска вещей по названию и описанию)
     *
     * @return List<ItemDto>
     */
    List<ItemGetDto> getItemsBySearch(String text, Long userId, Integer from, Integer size);


    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);
}