package ru.practicum.shareit.request.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRequestDao implements ItemRequestDao {

    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private long id;
    private UserDao inMemoryUserDao;
    private final ItemRequestMapper itemRequestMapper;

    /**
     * Метод генерирует и возвращает идентификатор запроса на вещь.
     * @return
     */
    private long getId() {
        return ++id;
    }


    @Override
    public ItemRequest addItemRequest(Long userId, ItemRequestInDto itemRequestDto) {

        if (itemRequests.get(itemRequestDto.getId()) != null)
            throw new AlreadyExistsException("Данный запрос уже зарегистрирован!");

        Long requestId = getId();
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, userId);

        itemRequests.put(requestId, itemRequest);

        log.debug("Запрос \"{}\" добавлен!", itemRequest.getDescription());
        return itemRequest;
    }


    @Override
    public ItemRequest updateItemRequest(Long userId, ItemRequestInDto itemRequestDto, Long requestId) {

        if (itemRequests.get(requestId) == null)
            throw new NotFoundException("Запрос с id=" + requestId + " не найден!");

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, userId);
        itemRequests.put(requestId, itemRequest);
        log.debug("Запрос \"{}\" обновлён!", itemRequestDto.getDescription());
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
