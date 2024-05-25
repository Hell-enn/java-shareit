package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Утилитарный класс содержит методы по преобразованию
 * объектов типа Booking в BookingDto и обратно.
 */
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getBookingStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Long bookingId, Item item, User user) {
        return new Booking(
                bookingId,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getBookingStatus()
        );
    }
}
