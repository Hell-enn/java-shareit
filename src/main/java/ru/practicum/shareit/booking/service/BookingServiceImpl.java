package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс BookingServiceImpl предоставляет функциональность по
 * взаимодействию со списком бронирований вещей -
 * объекты типа Booking
 * (добавление, удаление, вывод списка бронирований).
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ItemJpaRepository itemJpaRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingOutcomingDto postBooking(Long userId, BookingDto bookingDto) {
        validateNewBooking(bookingDto, userId);
        bookingDto.setStatus(BookingStatus.WAITING.getDescription());
        return bookingMapper.toBookingOutcomingDto(bookingJpaRepository.save(bookingMapper.toBooking(bookingDto, userId)));
    }


    @Override
    public BookingOutcomingDto patchBooking(Long userId, Boolean approved, Long bookingId) {
        validateUpdateBooking(bookingId, userId, approved);
        Optional<Booking> addedBookingOpt = bookingJpaRepository.findById(bookingId);
        Booking addedBooking = null;
        if (addedBookingOpt.isPresent()) {
            addedBooking = addedBookingOpt.get();
            addedBooking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookingJpaRepository.save(addedBooking);
        }
        return bookingMapper.toBookingOutcomingDto(addedBooking);
    }


    @Override
    public List<BookingOutcomingDto> getBookings(Long userId, String state) {
        if (!userJpaRepository.existsById(userId))
            throw new NotFoundException("Пользователь не найден!");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case "ALL": {
                bookings = bookingJpaRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            }
            case "CURRENT": {
                bookings = bookingJpaRepository.findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId, now, now);
                break;
            }
            case "PAST": {
                bookings = bookingJpaRepository.findPastBookings(userId, now);
                break;
            }
            case "FUTURE": {
                bookings = bookingJpaRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            }
            case "WAITING": {
                bookings = bookingJpaRepository.findWaitingBookings(userId);
                break;
            }
            case "REJECTED": {
                bookings = bookingJpaRepository.findRejectedBookings(userId);
                break;
            }
            default: throw new UnsupportedOperationException("{\"error\":\"Unknown state: " + state + "\"}", state);
        }
        List<BookingOutcomingDto> bookingDtoList = new ArrayList<>();
        bookings.forEach(booking -> bookingDtoList.add(bookingMapper.toBookingOutcomingDto(booking)));

        return bookingDtoList;
    }


    @Override
    public List<BookingOutcomingDto> getUserStuffBookings(Long userId, String state) {
        if (!userJpaRepository.existsById(userId))
            throw new NotFoundException("Пользователь не найден!");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case "ALL": {
                bookings = bookingJpaRepository.findAllStuffBookingsByOwnerId(userId);
                break;
            }
            case "CURRENT": {
                bookings = bookingJpaRepository.findCurrentStuffBookingsByOwnerId(userId, now);
                break;
            }
            case "PAST": {
                bookings = bookingJpaRepository.findPastStuffBookingsByOwnerId(userId, now);
                break;
            }
            case "FUTURE": {
                bookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(userId, now);
                break;
            }
            case "WAITING": {
                bookings = bookingJpaRepository.findWaitingStuffBookingsByOwnerId(userId);
                break;
            }
            case "REJECTED": {
                bookings = bookingJpaRepository.findRejectedStuffBookingsByOwnerId(userId);
                break;
            }
            default: throw new UnsupportedOperationException("{\"error\":\"Unknown state: " + state + "\"}", state);
        }
        List<BookingOutcomingDto> bookingDtoList = new ArrayList<>();
        bookings.forEach(booking -> bookingDtoList.add(bookingMapper.toBookingOutcomingDto(booking)));

        return bookingDtoList;
    }


    @Override
    public void deleteBooking(Long bookingId) {
        bookingJpaRepository.deleteById(bookingId);
    }


    @Override
    public BookingOutcomingDto getBooking(Long bookingId, Long userId) {
        validateGetBooking(bookingId, userId);
        Booking booking = bookingJpaRepository.findById(bookingId).get();
        return bookingMapper.toBookingOutcomingDto(booking);
    }


    /**
     * Закрытый служебный метод проверяет объект типа BookingDto
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа Booking при попытке его добавления
     * в репозиторий.
     * В случае неудачи выбрасывает исключение с сообщением об ошибке.
     *
     * @param bookingDto (приходящий объект бронирования)
     * @param userId (идентификатор пользователя, от чьего имени вносится бронирование)
     */
    private void validateNewBooking(BookingDto bookingDto, Long userId) {

        String message = "";
        if (bookingDto == null)
            throw new BadRequestException("Вы не передали информацию о бронировании!");

        if (!userJpaRepository.existsById(userId))
            throw new NotFoundException("Пользователь не найден!");

        Long itemId = bookingDto.getItemId();
        if (itemId == null)
            message = "Ссылка на вещь отсутствует!";
        else if (!itemJpaRepository.existsById(bookingDto.getItemId()))
            throw new NotFoundException("Вещь не найдена!");
        else {
            Boolean available = itemJpaRepository.findById(bookingDto.getItemId()).get().getAvailable();
            if (available != null && !available)
                message = "Вещь недоступна!";
        }

        Booking crossedBooking = bookingJpaRepository.findBookingForDate(itemId, bookingDto.getStart(), bookingDto.getEnd());
        if (crossedBooking != null)
            throw new NotFoundException("Найдено другое бронирование на эти даты!");

        Item item = itemJpaRepository.findById(itemId).get();
        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("Хозяин вещи не может её забронировать!");

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start != null && end != null && end.isBefore(start))
            message = "Дата начала бронирования не может превышать дату окончания!";
        else if (start != null && start.isBefore(LocalDateTime.now()))
            message = "Дата начала бронирования меньше текущего момента времени!";
        else if (start != null && start.equals(end))
            message = "Время окончания не может быть равна времени начала!";
        else if (start == null || end == null)
            message = "Время начала или окончания бронирования не задано!";

        if (!message.isBlank()) {
            throw new ValidationException(message);
        }
    }


    /**
     * Закрытый служебный метод проверяет объект типа Booking
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа Booking при попытке его обновления
     * в репозитории.
     * В случае неудачи выбрасывает исключение с сообщением об ошибке.
     *
     * @param bookingId (идентификатор бронирования)
     * @param userId (идентификатор пользователя, который вносит изменения в бронирование)
     * @param approved (флаг подтверждения бронирования пользователем)
     */
    private void validateUpdateBooking(Long bookingId, Long userId, Boolean approved) {
        if (!bookingJpaRepository.existsById(bookingId))
            throw new NotFoundException("Бронирование не найдено!");

        if (!userJpaRepository.existsById(userId))
            throw new NotFoundException("Пользователь не найден!");

        Booking booking = bookingJpaRepository.findById(bookingId).get();
        if (booking.getBookingStatus().equals(BookingStatus.APPROVED) && approved)
            throw new BadRequestException("Бронирование уже подтверждено!");

        Item item = booking.getItem();
        if (item == null)
            throw new NotFoundException("Отсутствует ссылка на вещь!");
        User owner = item.getOwner();
        if (owner == null)
            throw new NotFoundException("Отсутствует ссылка на хозяина вещи!");
        Long ownerId = owner.getId();
        if (!ownerId.equals(userId))
            throw new NotFoundException("Данный пользователь не может изменять статус бронирования!");
    }


    /**
     * Закрытый служебный метод проверяет объект типа Booking
     * на соответствие ряду условий. Используется впоследствии
     * для валидации пользователя и бронирования при попытке его
     * извлечения из репозитория.
     * В случае неудачи выбрасывает исключение с сообщением об ошибке.
     *
     * @param bookingId (идентификатор бронирования)
     * @param userId (идентификатор пользователя, который запрашивает информацию о бронировании)
     */
    private void validateGetBooking(Long bookingId, Long userId) {
        if (!bookingJpaRepository.existsById(bookingId))
            throw new NotFoundException("Бронирование не найдено!");

        if (!userJpaRepository.existsById(userId))
            throw new NotFoundException("Пользователь не найден!");

        Booking booking = bookingJpaRepository.findById(bookingId).get();
        Item item = booking.getItem();
        if (item == null)
            throw new NotFoundException("Отсутствует ссылка на вещь!");
        User owner = item.getOwner();
        if (owner == null)
            throw new NotFoundException("Отсутствует ссылка на хозяина вещи!");
        Long ownerId = owner.getId();
        User booker = booking.getBooker();
        if (booker == null)
            throw new NotFoundException("Отсутствует ссылка на автора бронирования!");
        Long bookerId = booker.getId();
        if (!ownerId.equals(userId) && !bookerId.equals(userId))
            throw new NotFoundException("Данный пользователь не может изменять статус бронирования!");
    }
}
