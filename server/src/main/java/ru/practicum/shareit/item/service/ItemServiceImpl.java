package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
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

    private final ItemPagingAndSortingRepository itemPagingAndSortingRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto postItem(Long userId, ItemDto itemDto) {
        validateNewItem(itemDto, userId);
        Item item = itemMapper.toItem(itemDto, userId);
        log.debug("Сохранение вещи с id={} в базу данных", itemDto.getId());
        return itemMapper.toItemDto(itemPagingAndSortingRepository.save(item));
    }


    @Override
    public ItemDto patchItem(Long userId, ItemDto itemDto, Long itemId) {
        Item addedItem = validateUpdateItem(userId, itemId, itemDto);
        itemMapper.updateItemFromDto(itemDto, addedItem);
        itemPagingAndSortingRepository.save(addedItem);
        log.debug("Вещь \"{}\" c id={} обновлена!", addedItem.getName(), itemDto.getId());
        return itemMapper.toItemDto(addedItem);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemGetDto> getItems(Long userId, Integer from, Integer size) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        int amountOfRequests = itemPagingAndSortingRepository.findItemsAmountByOwnerId(userId);
        int pageNum = amountOfRequests > from ? from / size : 0;

        Pageable page = PageRequest
                .of(pageNum, size)
                .toOptional()
                .orElseThrow(() -> new RuntimeException("Ошибка преобразования страницы!"));
        List<Item> items = itemPagingAndSortingRepository.findByUserId(userId, page);
        List<ItemGetDto> itemDtos = itemMapper.toItemGetDtos(items, userId);
        log.debug("Возвращаем список вещей пользователя с id={} в количестве {}", userId, itemDtos.size());
        return itemDtos;
    }


    @Override
    @Transactional(readOnly = true)
    public ItemGetDto getItem(Long id, Long userId) {
        Item addedItem = itemPagingAndSortingRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        log.debug("Возвращаем вещь с id={} пользователя с id={}", id, userId);
        return itemMapper.toItemGetDto(addedItem, userId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemGetDto> getItemsBySearch(String text, Long userId, Integer from, Integer size) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        if (text.isBlank()) {
            log.debug("В качестве подстроки для поиска пользователем с id={} передана пустая строка", userId);
            return new ArrayList<>();
        }

        int amountOfRequests = itemPagingAndSortingRepository.findAmountBySubstring(text, text);
        int pageNum = amountOfRequests > from ? from / size : 0;

        Pageable page = PageRequest
                .of(pageNum, size)
                .toOptional()
                .orElseThrow(() -> new RuntimeException("Ошибка преобразования страницы!"));
        List<Item> itemsBySubstring = itemPagingAndSortingRepository.findAllBySubstring(text, text, page);
        List<ItemGetDto> itemGetDtos = itemMapper.toItemGetDtos(itemsBySubstring, userId);
        log.debug("Передаем список вещей пользователя с id={} в количестве {}", userId, itemGetDtos.size());
        return itemGetDtos;
    }


    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        if (!itemPagingAndSortingRepository.existsById(itemId)) {
            log.debug("Объект типа Item с id={} отсутствует в базе данных", itemId);
            throw new NotFoundException("Вещь не найдена!");
        }
        if (bookingJpaRepository.findBookingByItemIdAndBookerId(
                itemId, userId, LocalDateTime.now()).isEmpty()) {
            log.debug("Пользователь с id={} не пользовался вещью с id={}", userId, itemId);
            throw new BadRequestException("Пользователь не брал вещь!");
        }
        if (commentDto != null && commentDto.getText().isBlank()) {
            log.debug("В объекте типа Comment в качестве отзыва передана пустая строка");
            throw new BadRequestException("В качестве отзыва передана пустая строка!");
        }
        log.debug("Публикуем отзыв от пользователя с id={} вещи с id={}", userId, itemId);
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
        else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.debug("Поле description для вещи \"{}\" не передано совсем или передана пустая строка", itemDto.getName());
            throw new BadRequestException("Описание вещи отсутствует!");
        } else if (itemDto.getAvailable() == null) {
            log.debug("Не передан статус вещи {} пользователя с id={}", itemDto.getName(), userId);
            throw new BadRequestException("Статус вещи отсутствует!");
        } else if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных", userId);
            throw new NotFoundException("Пользователь не существует!");
        }

        if (!message.isBlank()) {
            log.debug(message);
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

        if (itemDto == null) {
            log.debug("Объект типа ItemDto не передан(null)");
            throw new NotFoundException("Вы не передали информацию о вещи!");
        }

        Item addedItem = itemPagingAndSortingRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        Long ownerId = addedItem.getOwner().getId();

        if (!userId.equals(ownerId)) {
            log.debug("Пользователь с id={} не является обладателем вещи с id={}", userId, itemId);
            throw new ForbiddenException("Пользователь не является обладателем вещи!");
        }
        if (itemDto.getOwner() != null && !addedItem.getOwner().getId().equals(itemDto.getOwner())) {
            log.debug("Попытка изменить информацию о хозяине вещи с id={}", itemId);
            throw new BadRequestException("Нельзя изменить хозяина вещи!");
        }
        if (itemDto.getRequestId() != null && !addedItem.getRequest().getId().equals(itemDto.getRequestId())) {
            log.debug("Попытка изменить информацию о запросе вещи с id={}", itemId);
            throw new BadRequestException("Нельзя изменить информацию о запросе к вещи!");
        }
        return addedItem;
    }
}
