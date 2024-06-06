package ru.practicum.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.Optional;

/**
 * Утилитарный класс содержит методы по преобразованию
 * объектов типа Booking в BookingOutcomingDto и обратно.
 */
@AllArgsConstructor
@Component
public class BookingMapper {
    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BookingOutcomingDto toBookingOutcomingDto(Booking booking) {
        if (booking != null) {
            Item item = booking.getItem();
            return new BookingOutcomingDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    itemMapper.toItemDto(item),
                    userMapper.toUserDto(booking.getBooker()),
                    booking.getBookingStatus() != null ? booking.getBookingStatus().getDescription() : null
            );
        }
        return null;
    }

    public Booking toBooking(BookingDto bookingDto, Long userId) {
        Optional<User> userOpt = userJpaRepository.findById(userId);
        User user = null;
        if (userOpt.isPresent())
            user = userOpt.get();
        Long itemId = bookingDto.getItemId();
        Item item = null;
        if (itemId != null) {
            Optional<Item> itemOpt = itemJpaRepository.findById(itemId);
            if (itemOpt.isPresent())
                item = itemOpt.get();
        }
        BookingStatus bookingStatus = null;
        switch (bookingDto.getStatus()) {
            case "WAITING": {
                bookingStatus = BookingStatus.WAITING;
                break;
            }
            case "APPROVED": {
                bookingStatus = BookingStatus.APPROVED;
                break;
            }
            case "REJECTED": {
                bookingStatus = BookingStatus.REJECTED;
                break;
            }
            case "CANCELED": {
                bookingStatus = BookingStatus.CANCELED;
                break;
            }
        }
        return new Booking(
                0,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingStatus
        );
    }
}
