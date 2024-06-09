package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс ItemService предоставляет функциональность по
 * взаимодействию со списком вещей (бизнес-логика) -
 * объекты типа Item
 * (добавление, удаление, вывод списка вещей).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository itemJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto postItem(Long userId, ItemDto itemDto) {
        validateNewItem(itemDto, userId);
        Item item = itemMapper.toItem(itemDto, userId);
        return itemMapper.toItemDto(itemJpaRepository.save(item));
    }


    @Override
    public ItemDto patchItem(Long userId, ItemDto itemDto, Long itemId) {
        Item addedItem = validateUpdateItem(userId, itemId, itemDto);
        itemMapper.updateItemFromDto(itemDto, addedItem);
        itemJpaRepository.save(addedItem);
        log.debug("Вещь \"{}\" обновлена!", addedItem.getName());
        return itemMapper.toItemDto(addedItem);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemGetDto> getItems(Long userId) {
        List<Item> items = itemJpaRepository.findByUserId(userId);
        List<ItemGetDto> itemDtos = new ArrayList<>();
        items.forEach(item -> itemDtos.add(itemMapper.toItemGetDto(item, userId)));
        return itemDtos;
    }


    @Override
    public void deleteItem(Long itemId) {
        itemJpaRepository.deleteById(itemId);
    }


    @Override
    @Transactional(readOnly = true)
    public ItemGetDto getItem(Long id, Long userId) {
        Item addedItem = itemJpaRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        return itemMapper.toItemGetDto(addedItem, userId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemGetDto> getItemsBySearch(String text, Long userId) {
        if (text.isBlank())
            return new ArrayList<>();

        List<Item> itemsByName = itemJpaRepository.findAllByNameContainingIgnoreCaseAndAvailableTrue(text);
        List<Item> itemsByDescription = itemJpaRepository.findAllByDescriptionContainingIgnoreCaseAndAvailableTrue(text);
        Map<Long, ItemGetDto> itemDtoList = new HashMap<>();

        itemsByName.forEach(item -> {
            ItemGetDto itemGetDto = itemMapper.toItemGetDto(item, userId);
            itemDtoList.put(itemGetDto.getId(), itemGetDto);
        });

        itemsByDescription.forEach(item -> {
            ItemGetDto itemGetDto = itemMapper.toItemGetDto(item, userId);
            itemDtoList.put(itemGetDto.getId(), itemGetDto);
        });

        return new ArrayList<>(itemDtoList.values());
    }


    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        if (!itemJpaRepository.existsById(itemId))
            throw new NotFoundException("Вещь не найдена!");
        if (bookingJpaRepository.findBookingByItemIdAndBookerId(
                itemId, userId, LocalDateTime.now()).isEmpty())
            throw new BadRequestException("Пользователь не брал вещь!");
        if (commentDto != null && commentDto.getText().isBlank())
            throw new BadRequestException("В качестве отзыва передана пустая строка!");
        return commentMapper.toCommentDto(commentJpaRepository.save(commentMapper.toComment(commentDto, userId, itemId)));
    }


    /**
     * Закрытый служебный метод проверяет объект типа Item
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа ItemDto при попытке его добавления
     * в списке.
     * В случае неудачи выбрасывает исключение ValidationException
     * с сообщением об ошибке.
     *
     * @param itemDto (объект валидации)
     */
    private void validateNewItem(ItemDto itemDto, Long userId) {

        String message = "";

        if (itemDto == null)
            message = "Вы не передали информацию о вещи!";
        else if (itemDto.getName() == null || itemDto.getName().isBlank())
            message = "Название вещи отсутствует!";
        else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank())
            throw new BadRequestException("Описание вещи отсутствует!");
        else if (itemDto.getAvailable() == null)
            throw new BadRequestException("Статус вещи отсутствует!");
        else if (!userJpaRepository.existsById(userId))
            throw new NotFoundException("Пользователь не существует!");

        if (!message.isBlank()) {
            throw new ValidationException(message);
        }
    }


    /**
     * Закрытый служебный метод проверяет объект типа Item
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа ItemDto при попытке его обновления в списке.
     * В случае неудачи выбрасывает исключение ValidationException
     * с сообщением об ошибке.
     *
     * @param itemDto (объект валидации)
     */
    private Item validateUpdateItem(Long userId, Long itemId, ItemDto itemDto) {

        if (itemDto == null)
            throw new NotFoundException("Вы не передали информацию о вещи!");

        Item addedItem = itemJpaRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        Long ownerId = addedItem.getOwner().getId();

        if (!userId.equals(ownerId))
            throw new ForbiddenException("Пользователь не является обладателем вещи!");
        if (itemDto.getOwner() != null && !addedItem.getOwner().getId().equals(itemDto.getOwner()))
            throw new BadRequestException("Нельзя изменить хозяина вещи!");
        if (itemDto.getRequest() != null && !addedItem.getRequest().getId().equals(itemDto.getRequest()))
            throw new BadRequestException("Нельзя изменить информацию о запросе к вещи!");
        return addedItem;
    }
}
