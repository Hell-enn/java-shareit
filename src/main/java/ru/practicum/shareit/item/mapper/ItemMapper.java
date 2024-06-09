package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитарный класс содержит методы по преобразованию
 * объектов типа Item в ItemDto и обратно.
 */
@AllArgsConstructor
@Component
public class ItemMapper {
    private final UserJpaRepository userJpaRepository;
    private final BookingJpaRepository bookingJpaRepository;
    private final RequestJpaRepository requestJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final CommentMapper commentMapper;

    public ItemDto toItemDto(Item item) {
        List<Comment> comments = commentJpaRepository.findByItemId(item.getId());
        List<String> commentTexts = new ArrayList<>();
        comments.forEach(comment -> commentTexts.add(comment.getText()));
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                commentTexts
        );
    }

    public Item toItem(ItemDto itemDto, Long userId) {
        User user = userJpaRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Long requestId = itemDto.getRequest();
        ItemRequest itemRequest = null;
        if (requestId != null)
            itemRequest = requestJpaRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден!"));
        return new Item(
                0,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }


    public ItemGetDto toItemGetDto(Item item, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Booking prevBooking = bookingJpaRepository.findPreviousUserBooking(userId, now, item.getId());
        Booking nextBooking = bookingJpaRepository.findNextUserBooking(userId, now, item.getId());
        BookingDto prevOutBooking = null;
        if (prevBooking != null)
            prevOutBooking = new BookingDto(
                    prevBooking.getId(),
                    prevBooking.getStart(),
                    prevBooking.getEnd(),
                    prevBooking.getItem() != null ? prevBooking.getItem().getId() : null,
                    prevBooking.getBooker() != null ? prevBooking.getBooker().getId() : null,
                    prevBooking.getBookingStatus().getDescription());
        BookingDto nextOutBooking = null;
        if (nextBooking != null)
            nextOutBooking = new BookingDto(
                    nextBooking.getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd(),
                    nextBooking.getItem() != null ? nextBooking.getItem().getId() : null,
                    nextBooking.getBooker() != null ? nextBooking.getBooker().getId() : null,
                    nextBooking.getBookingStatus().getDescription());
        List<Comment> comments = commentJpaRepository.findByItemId(item.getId());
        List<CommentDto> commentDtoList = new ArrayList<>();
        comments.forEach(comment -> commentDtoList.add(commentMapper.toCommentDto(comment)));
        return new ItemGetDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                prevOutBooking,
                nextOutBooking,
                commentDtoList
        );
    }


    public void updateItemFromDto(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank() && !item.getName().equals(itemDto.getName()))
            item.setName(itemDto.getName());

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()
                && !item.getDescription().equals(itemDto.getDescription()))
            item.setDescription(itemDto.getDescription());

        if (itemDto.getAvailable() != null && !item.getAvailable().equals(itemDto.getAvailable()))
            item.setAvailable(itemDto.getAvailable());
    }
}