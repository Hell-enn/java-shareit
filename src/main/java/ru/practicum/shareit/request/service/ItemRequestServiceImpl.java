package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestDao inMemoryItemRequestDao;


    public ItemRequestDto postItemRequest(Long userId, ItemRequestDto itemRequestDto) {

        validateItemRequest(itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(inMemoryItemRequestDao.addItemRequest(userId, itemRequestDto));
    }


    public ItemRequestDto patchItemRequest(Long userId, ItemRequestDto itemRequestDto, Long requestId) {
        return ItemRequestMapper.toItemRequestDto(inMemoryItemRequestDao.updateItemRequest(userId, itemRequestDto, requestId));
    }


    public List<ItemRequestDto> getItemRequests() {

        List<ItemRequest> itemRequests = inMemoryItemRequestDao.getItemRequests();
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

        for (ItemRequest itemRequest: itemRequests) {
            itemRequestDtoList.add(ItemRequestMapper.toItemRequestDto(itemRequest));
        }

        return itemRequestDtoList;

    }


    public void deleteItemRequest(long itemRequestId) {
        inMemoryItemRequestDao.deleteItemRequest(itemRequestId);
    }


    public ItemRequestDto getItemRequest(long id) {

        ItemRequest itemRequest = inMemoryItemRequestDao.getItemRequest(id);
        if (itemRequest == null)
            throw new NotFoundException("Запрос " + id + " отсутствует в списке!");

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }


    /**
     * Закрытый служебный метод проверяет объект типа ItemRequest
     * на соответствие ряду условий. Используется впоследствие
     * для валидации объекта типа ItemRequest при попытке его добавления
     * или обновления в списке.
     * В случае неудачи выбрасывает исключение ValidationException
     * с сообщением об ошибке.
     *
     * @param itemRequestDto ()
     */
    private void validateItemRequest(ItemRequestDto itemRequestDto) {

        String message = "";

        if (itemRequestDto == null)
            message = "Вы не передали информацию о запросе!";
        else if (itemRequestDto.getRequestor() == null)
            message = "Имя пользователя отсутствует!";
        else if (itemRequestDto.getCreated() == null)
            message = "Вы не передали информацию о времени создания запроса!";

        if (!message.isBlank()) {
            throw new ValidationException(message);
        }

    }
}
