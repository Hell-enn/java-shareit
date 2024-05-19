package ru.practicum.shareit.request.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class InMemoryItemRequestDao implements ItemRequestDao {

    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private int id;

    public InMemoryItemRequestDao() {
    }

    /**
     * Метод генерирует и возвращает идентификатор запроса на вещь.
     * @return
     */
    private long getId() {
        return ++id;
    }


    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest) {

        if (itemRequests.get(itemRequest.getId()) != null)
            throw new AlreadyExistsException("Данный запрос уже зарегистрирован!");

        itemRequest.setId(getId());
        itemRequests.put(itemRequest.getId(), itemRequest);

        log.debug("Запрос \"{}\" добавлен!", itemRequest.getDescription());
        return itemRequest;
    }


    @Override
    public ItemRequest updateItemRequest(ItemRequest itemRequest) {

        if (itemRequests.get(itemRequest.getId()) == null)
            throw new NotFoundException("Запрос с id=" + itemRequest.getId() + " не найден!");

        log.debug("Запрос \"{}\" обновлён!", itemRequest.getDescription());
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }


    @Override
    public void deleteItemRequest(long id) {
        log.info("Удаление запроса по id: {}", id);
        itemRequests.remove(id);
    }


    @Override
    public ItemRequest getItemRequest(long id) {
        ItemRequest itemRequest = itemRequests.get(id);
        if (itemRequest == null)
            throw new NotFoundException("Запрос с id=" + id + " не найден!");

        return itemRequest;
    }


    @Override
    public boolean containsItemRequest(long id) {
        return itemRequests.containsKey(id);
    }


    @Override
    public List<ItemRequest> getItemRequests() {
        return new ArrayList<>(itemRequests.values());
    }
}
