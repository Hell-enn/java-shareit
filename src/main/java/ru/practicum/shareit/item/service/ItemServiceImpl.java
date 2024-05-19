package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс ItemService предоставляет функциональность по
 * взаимодействию со списком вещей (бизнес-логика) -
 * объекты типа Item
 * (добавление, удаление, вывод списка вещей).
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao inMemoryItemDao;

    @Override
    public ItemDto postItem(Long userId, ItemDto itemDto) {
        validateItem(itemDto);
        return ItemMapper.toItemDto(inMemoryItemDao.addItem(userId, itemDto));
    }


    @Override
    public ItemDto patchItem(Long userId, ItemDto item, Long itemId) {
        return ItemMapper.toItemDto(inMemoryItemDao.updateItem(userId, item, itemId));
    }


    @Override
    public List<ItemDto> getItems(Long userId) {
        List<Item> items = inMemoryItemDao.getItems(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item: items) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }

        return itemDtoList;
    }


    @Override
    public void deleteItem(Long itemId) {
        inMemoryItemDao.deleteItem(itemId);
    }


    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(inMemoryItemDao.getItem(id));
    }


    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        List<Item> items = inMemoryItemDao.getItemsBySearch(text);
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item: items) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }

        return itemDtoList;
    }


    /**
     * Закрытый служебный метод проверяет объект типа Item
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа ItemDto при попытке его добавления
     * или обновления в списке.
     * В случае неудачи выбрасывает исключение ValidationException
     * с сообщением об ошибке.
     *
     * @param itemDto (объект валидации)
     */
    private void validateItem(ItemDto itemDto) {

        String message = "";

        if (itemDto == null)
            message = "Вы не передали информацию о вещи!";
        else if (itemDto.getName() == null)
            message = "Название вещи отсутствует!";
        else if (itemDto.getDescription() == null)
            message = "Описание вещи отсутствует!";
        else if (itemDto.getAvailable() == null)
            message = "Статус вещи отсутствует!";

        if (!message.isBlank()) {
            throw new ValidationException(message);
        }

    }
}
