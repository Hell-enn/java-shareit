package ru.practicum.shareit.item.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Класс InMemoryItemDao - реализация интерфейса ItemDao.
 * Предоставляет хранение данных о вещах пользователей приложения
 * только во время выполнения силами структуры данных HashMap.
 */
@Component
@Slf4j
@AllArgsConstructor
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private long id;
    @Autowired
    private UserDao inMemoryUserDao;
    @Autowired
    private ItemRequestDao inMemoryItemRequestDao;

    public InMemoryItemDao() {
    }


    /**
     * Закрытый служебный метод генерирует и возвращает идентификатор вещи.
     * @return Long
     */
    private long getId() {
        return ++id;
    }


    @Override
    public Item addItem(Long userId, ItemDto itemDto) {

        Long requestId = itemDto.getRequest();
        ItemRequest itemRequest = requestId != null ? inMemoryItemRequestDao.getItemRequest(requestId) : null;
        User user = inMemoryUserDao.getUser(userId);

        Long itemId = getId();
        Item newItem = ItemMapper.toItem(itemDto, user, itemRequest, itemId);

        items.put(itemId, newItem);

        log.debug("Вещь {} добавлена!", newItem.getName());
        return newItem;

    }


    @Override
    public Item updateItem(Long userId, ItemDto itemDto, Long itemId) {

        Item addedItem = items.get(itemId);

        if (addedItem == null)
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена!");

        Long ownerId = addedItem.getOwner() != null ? addedItem.getOwner().getId() : null;
        if (!userId.equals(ownerId))
            throw new ForbiddenException("Пользователь не является обладателем вещи!");

        if (itemDto.getName() != null && !itemDto.getName().isBlank() && !addedItem.getName().equals(itemDto.getName()))
            addedItem.setName(itemDto.getName());

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()
                && !addedItem.getDescription().equals(itemDto.getDescription()))
            addedItem.setDescription(itemDto.getDescription());

        if (itemDto.getAvailable() != null && !addedItem.getAvailable().equals(itemDto.getAvailable()))
            addedItem.setAvailable(itemDto.getAvailable());

        items.put(addedItem.getId(), addedItem);
        log.debug("Вещь \"{}\" обновлена!", addedItem.getName());
        return addedItem;
    }


    @Override
    public void deleteItem(Long itemId) {
        log.info("Удаление вещи по id: {}", itemId);
        items.remove(itemId);
    }


    @Override
    public Item getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null)
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена!");

        return item;
    }


    @Override
    public boolean containsItem(Long id) {
        return items.containsKey(id);
    }


    @Override
    public List<Item> getItems(Long userId) {

        List<Item> itemsOfCurrentUser = new ArrayList<>();

        for (Item item: items.values()) {
            Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
            if (ownerId != null && ownerId.equals(userId))
                itemsOfCurrentUser.add(item);
        }

        return itemsOfCurrentUser;
    }


    @Override
    public List<Item> getItemsBySearch(String text) {

        List<Item> usersItems = new ArrayList<>();

        if (text.isBlank())
            return usersItems;

        for (Item item: items.values()) {
            String name = item.getName();
            String description = item.getDescription();
            if (((name != null && name.toLowerCase().contains(text.toLowerCase()))
                    || (description != null && description.toLowerCase().contains(text.toLowerCase())))
                    && item.getAvailable())
                usersItems.add(item);
        }

        return usersItems;
    }
}
