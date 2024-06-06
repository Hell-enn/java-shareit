package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryBookingDao implements BookingDao {

    private final Map<Long, Booking> bookings = new HashMap<>();
    private final BookingMapper bookingMapper;
    private int id;
    private UserDao inMemoryUserDao;
    private ItemDao inMemoryItemDao;

    /**
     * Метод генерирует и возвращает идентификатор бронирования.
     * @return
     */
    private long getId() {
        return ++id;
    }


    @Override
    public Booking addBooking(Long userId, BookingDto bookingDto) {

        Booking newBooking = bookingMapper.toBooking(bookingDto, userId);

        bookings.put(newBooking.getId(), newBooking);

        log.debug("Бронирование вещи {} добавлено!", newBooking.getItem());
        return newBooking;

    }


    @Override
    public Booking updateBooking(Long userId, BookingDto booking, Long bookingId) {

        Booking addedBooking = bookings.get(bookingId);

        if (addedBooking == null)
            throw new NotFoundException("Бронирование с id=" + bookingId + " не найдено!");

        Long bookerId = addedBooking.getBooker() != null ? addedBooking.getBooker().getId() : null;
        if (!userId.equals(bookerId))
            throw new BadRequestException("Пользователь не является автором бронирования!");

        Long addedItemId = addedBooking.getItem().getId();
        if (!booking.getItemId().equals(addedItemId))
            throw new BadRequestException("Попытка изменить вещь, в отношении которой создано бронирование!");

        Booking newBooking = bookingMapper.toBooking(booking, userId);

        bookings.put(newBooking.getId(), newBooking);
        log.debug("Бронирование \"{}\" обновлено!", newBooking.getId());
        return newBooking;
    }


    @Override
    public void deleteBooking(Long id) {
        log.info("Удаление бронирования по id: {}", id);
        bookings.remove(id);
    }


    @Override
    public Booking getBooking(Long id) {
        Booking booking = bookings.get(id);
        if (booking == null)
            throw new NotFoundException("Бронирование с id=" + id + " не найдено!");

        return booking;
    }


    @Override
    public boolean containsBooking(Long id) {
        return bookings.containsKey(id);
    }


    @Override
    public List<Booking> getBookings(Long userId) {

        List<Booking> bookingsOfCurrentUser = new ArrayList<>();

        for (Booking booking: bookings.values()) {
            Long ownerId = booking.getBooker() != null ? booking.getBooker().getId() : null;
            if (ownerId != null && ownerId.equals(userId))
                bookingsOfCurrentUser.add(booking);
        }

        return bookingsOfCurrentUser;
    }
}
