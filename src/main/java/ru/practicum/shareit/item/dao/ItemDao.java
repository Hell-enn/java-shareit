package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс-хранилище ItemDao определяет контракт,
 * включающий сигнатуры ряда методов, которые реализуют
 * расширяющие его классы слоя манипуляции данными на уровне хранилища
 * в части работы с арендуемыми вещами приложения ShareIt.
 */
public interface ItemDao {

    /**
     * Преобразует объект типа ItemDto в Item и добавляет его в хранилище.
     *
     * @param userId (идентификатор пользователя - хозяина вещи)
     * @param itemDto (объект, содержащий информацию о новом пользователе)
     *
     * @return Item
     */
    Item addItem(Long userId, ItemDto itemDto);


    /**
     * Обновляет объект типа Item, уже содержащийся в хранилище,
     * значениями, привнесёнными объектом типа ItemDto, переданным в качестве параметра.
     *
     * @param userId (идентификатор пользователя, который отправил запрос на обновление информации о вещи)
     * @param itemDto (объект, содержащий обновленную информацию о вещи)
     * @param itemId (идентификатор вещи, информацию о которой необходимо обновить)
     *
     * @return Item
     */
    Item updateItem(Long userId, ItemDto itemDto, Long itemId);


    /**
     * Удаляет объект типа Item с идентификатором itemId из хранилища.
     *
     * @param itemId (идентификатор вещи)
     */
    void deleteItem(Long itemId);


    /**
     * Возвращает из хранилища объект типа Item по его идентификатору itemId.
     *
     * @param itemId (идентификатор вещи)
     *
     * @return Item
     */
    Item getItem(Long itemId);


    /**
     * Отвечает на вопрос, содержится ли объект типа Item с
     * идентификатором itemId в хранилище.
     *
     * @param itemId (идентификатор вещи)
     *
     * @return boolean
     */
    boolean containsItem(Long itemId);


    /**
     * Возвращает список, содержащий все объекты типа Item,
     * принадлежащие пользователю с идентификатором userId из хранилища.
     *
     * @param userId (идентификатор пользователя)
     *
     * @return List<Item>
     */
    List<Item> getItems(Long userId);


    /**
     * Возвращает вещи по результатам полнотекстового поиска по названию и описанию вещи.
     *
     * @param text (текст запроса)
     *
     * @return List<Item>
     */
    List<Item> getItemsBySearch(String text);
}