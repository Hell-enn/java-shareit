package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс BookingServiceImpl предоставляет функциональность по
 * взаимодействию со списком бронирований вещей -
 * объекты типа Booking
 * (добавление, удаление, вывод списка бронирований).
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingDao inMemoryBookingDao;

    public BookingDto postBooking(Long userId, BookingDto bookingDto) {

        validateBooking(bookingDto);
        return BookingMapper.toBookingDto(inMemoryBookingDao.addBooking(userId, bookingDto));
    }


    public BookingDto patchBooking(Long userId, BookingDto booking, Long bookingId) {
        return BookingMapper.toBookingDto(inMemoryBookingDao.updateBooking(userId, booking, bookingId));
    }


    public List<BookingDto> getBookings(Long userId) {
        List<Booking> bookings = inMemoryBookingDao.getBookings(userId);
        List<BookingDto> bookingDtoList = new ArrayList<>();

        for (Booking booking: bookings) {
            bookingDtoList.add(BookingMapper.toBookingDto(booking));
        }

        return bookingDtoList;
    }


    public void deleteBooking(long bookingId) {
        inMemoryBookingDao.deleteBooking(bookingId);
    }


    public BookingDto getBooking(long id) {
        return BookingMapper.toBookingDto(inMemoryBookingDao.getBooking(id));
    }


    /**
     * Закрытый служебный метод проверяет объект типа Booking
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа Booking при попытке его добавления
     * или обновления в списке.
     * В случае неудачи выбрасывает исключение ValidationException
     * с сообщением об ошибке.
     *
     * @param bookingDto ()
     */
    private void validateBooking(BookingDto bookingDto) {

        String message = "";

        if (bookingDto == null)
            message = "Вы не передали информацию о бронировании!";
        else if (bookingDto.getItem() == null)
            message = "Ссылка на вещь отсутствует!";
        else if (bookingDto.getBooker() == null)
            message = "Ссылка на пользователя, сформировавшего бронирование, отсутствует!";

        if (!message.isBlank()) {
            throw new ValidationException(message);
        }

    }
}
