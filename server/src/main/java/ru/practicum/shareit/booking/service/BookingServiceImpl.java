package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
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
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingJpaRepository bookingJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ItemPagingAndSortingRepository itemPagingAndSortingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingOutcomingDto postBooking(Long userId, BookingDto bookingDto) {
        validateNewBooking(bookingDto, userId);
        bookingDto.setStatus(BookingStatus.WAITING.getDescription());
        log.debug("Сохранение бронирования для вещи с id={} в базу данных", bookingDto.getItemId());
        return bookingMapper.toBookingOutcomingDto(bookingJpaRepository.save(bookingMapper.toBooking(bookingDto, userId)));
    }


    @Override
    public BookingOutcomingDto patchBooking(Long userId, Boolean approved, Long bookingId) {
        Booking addedBooking = validateUpdateBooking(bookingId, userId, approved);
        addedBooking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.debug("Обновление бронирования с id={} в базе данных", bookingId);
        bookingJpaRepository.save(addedBooking);
        return bookingMapper.toBookingOutcomingDto(addedBooking);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingOutcomingDto> getBookings(Long userId, String state, Integer from, Integer size) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        int amountOfRequests = bookingJpaRepository.findAmountByBookerId(userId);
        int pageNum = amountOfRequests > from ? from / size : 0;

        Pageable page = PageRequest
                .of(pageNum, size)
                .toOptional()
                .orElseThrow(() -> new RuntimeException("Ошибка преобразования страницы!"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case "ALL": {
                bookings = bookingJpaRepository.findByBookerIdOrderByStartDesc(userId, page);
                break;
            }
            case "CURRENT": {
                bookings = bookingJpaRepository.findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId, now, now, page);
                break;
            }
            case "PAST": {
                bookings = bookingJpaRepository.findPastBookings(userId, now, page);
                break;
            }
            case "FUTURE": {
                bookings = bookingJpaRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now, page);
                break;
            }
            case "WAITING": {
                bookings = bookingJpaRepository.findWaitingBookings(userId, page);
                break;
            }
            case "REJECTED": {
                bookings = bookingJpaRepository.findRejectedBookings(userId, page);
                break;
            }
            default: throw new UnsupportedOperationException("{\"error\":\"Unknown state: " + state + "\"}", state);
        }
        List<BookingOutcomingDto> bookingOutcomingDtoList = bookingMapper.bookingOutcomingDtoList(bookings);
        log.debug("Возвращаем список бронирований пользователя с id={} в количестве {}", userId, bookings.size());
        return bookingOutcomingDtoList;
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingOutcomingDto> getUserStuffBookings(Long userId, String state, Integer from, Integer size) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        int amountOfRequests = bookingJpaRepository.findStuffBookingsAmountByOwnerId(userId);
        int pageNum = amountOfRequests > from ? from / size : 0;

        Pageable page = PageRequest
                .of(pageNum, size)
                .toOptional()
                .orElseThrow(() -> new RuntimeException("Ошибка преобразования страницы!"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case "ALL": {
                bookings = bookingJpaRepository.findAllStuffBookingsByOwnerId(userId, page);
                break;
            }
            case "CURRENT": {
                bookings = bookingJpaRepository.findCurrentStuffBookingsByOwnerId(userId, now, page);
                break;
            }
            case "PAST": {
                bookings = bookingJpaRepository.findPastStuffBookingsByOwnerId(userId, now, page);
                break;
            }
            case "FUTURE": {
                bookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(userId, now, page);
                break;
            }
            case "WAITING": {
                bookings = bookingJpaRepository.findWaitingStuffBookingsByOwnerId(userId, page);
                break;
            }
            case "REJECTED": {
                bookings = bookingJpaRepository.findRejectedStuffBookingsByOwnerId(userId, page);
                break;
            }
            default: throw new UnsupportedOperationException("{\"error\":\"Unknown state: " + state + "\"}", state);
        }
        List<BookingOutcomingDto> bookingOutcomingDtoList = bookingMapper.bookingOutcomingDtoList(bookings);
        log.debug("Возвращаем список бронирований вещей пользователя с id={} в количестве {}", userId, bookings.size());
        return bookingOutcomingDtoList;
    }


    @Override
    @Transactional(readOnly = true)
    public BookingOutcomingDto getBooking(Long bookingId, Long userId) {
        Booking booking = validateGetBooking(bookingId, userId);
        log.debug("Возвращаем бронирование с id={}", bookingId);
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

        if (bookingDto == null) {
            log.debug("Для валидации нового бронирования не передан объект типа BookingDto(null)");
            throw new BadRequestException("Вы не передали информацию о бронировании!");
        }

        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        Long itemId = bookingDto.getItemId();
        if (itemId == null) {
            log.debug("Не передан id объекта типа Item, для которого добавляется объект типа Booking");
            throw new ValidationException("Ссылка на вещь отсутствует!");
        }
        Optional<Item> itemOpt = itemPagingAndSortingRepository.findById(itemId);
        Boolean available = itemOpt
                    .orElseThrow(() -> new NotFoundException("Вещь не найдена!"))
                    .getAvailable();
        if (available != null && !available) {
            log.debug("Статус объекта типа Item, для которого добавляется объект Booking - 'not available'");
            throw new ValidationException("Вещь недоступна!");
        }

        Booking crossedBooking = bookingJpaRepository.findBookingForDate(itemId, bookingDto.getStart(), bookingDto.getEnd());
        if (crossedBooking != null) {
            log.debug("Попытка добавления объекта типа Booking с датами, пересекающимися с уже существующими объектами");
            throw new NotFoundException("Найдено другое бронирование на эти даты!");
        }

        Item item = itemOpt.get();
        if (item.getOwner().getId().equals(userId)) {
            log.debug("Попытка добавления объекта типа Booking хозяином вещи");
            throw new NotFoundException("Хозяин вещи не может её забронировать!");
        }

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        String message = "";
        if (start != null && end != null && end.isBefore(start))
            message = "Дата начала бронирования не может превышать дату окончания!";
        else if (start != null && start.isBefore(LocalDateTime.now()))
            message = "Дата начала бронирования меньше текущего момента времени!";
        else if (start != null && start.equals(end))
            message = "Время окончания не может быть равно времени начала!";
        else if (start == null || end == null)
            message = "Время начала или окончания бронирования не задано!";

        if (!message.isBlank()) {
            log.debug(message);
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
    private Booking validateUpdateBooking(Long bookingId, Long userId, Boolean approved) {
        if (!bookingJpaRepository.existsById(bookingId)) {
            log.debug("Объект типа Booking с id={} отсутствует в базе данных", bookingId);
            throw new NotFoundException("Бронирование не найдено!");
        }

        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        Booking booking = bookingJpaRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено!"));
        if (booking.getBookingStatus().equals(BookingStatus.APPROVED) && approved) {
            log.debug("Попытка одобрить бронирование с id={}, которое уже было одобрено", bookingId);
            throw new BadRequestException("Бронирование уже подтверждено!");
        }

        Item item = booking.getItem();
        if (item == null) {
            log.debug("В бронировании с id={} отсутствует информация о вещи", bookingId);
            throw new NotFoundException("Отсутствует ссылка на вещь!");
        }
        User owner = item.getOwner();
        if (owner == null) {
            log.debug("В бронировании с id={} отсутствует информация о хозяине вещи", bookingId);
            throw new NotFoundException("Отсутствует ссылка на хозяина вещи!");
        }
        Long ownerId = owner.getId();
        if (!ownerId.equals(userId)) {
            log.debug("Попытка пользователем с id={}, не являющимся хозяином вещи, изменить статус бронирования с id={}",
                    userId, bookingId);
            throw new NotFoundException("Данный пользователь не может изменять статус бронирования!");
        }
        return booking;
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
    private Booking validateGetBooking(Long bookingId, Long userId) {
        Booking booking = bookingJpaRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено!"));
        Item item = booking.getItem();
        if (item == null) {
            log.debug("В бронировании с id={} отсутствует информация о вещи", bookingId);
            throw new NotFoundException("Отсутствует ссылка на вещь!");
        }
        User owner = item.getOwner();
        if (owner == null) {
            log.debug("В бронировании с id={} отсутствует информация о хозяине вещи", bookingId);
            throw new NotFoundException("Отсутствует ссылка на хозяина вещи!");
        }
        Long ownerId = owner.getId();
        User booker = booking.getBooker();
        if (booker == null) {
            log.debug("В бронировании с id={} отсутствует информация об авторе бронирования вещи", bookingId);
            throw new NotFoundException("Отсутствует ссылка на автора бронирования!");
        }
        Long bookerId = booker.getId();
        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            log.debug("Пользователь с id={} не может получить информацию о бронировании, " +
                    "поскольку не является хозяином вещи или автором бронирования", userId);
            throw new NotFoundException("Данный пользователь не может получить информацию о бронировании!");
        }
        return booking;
    }
}
