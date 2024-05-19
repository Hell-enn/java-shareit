package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    /**
     * Метод добавляет новое бронирование в список в случае,
     * если оно в нём отсутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает добавленный объект.
     *
     * @param bookingDto
     * @return
     */
    BookingDto postBooking(Long userId, BookingDto bookingDto);


    /**
     * Метод обновляет объект бронирования в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param booking
     * @return
     */
    BookingDto patchBooking(Long userId, BookingDto booking, Long bookingId);


    /**
     * Метод возвращает список бронирований из хранилища.
     *
     * @return
     */
    List<BookingDto> getBookings(Long userId);


    /**
     * Метод удаляет бронирование с bookingId из хранилища
     */
    void deleteBooking(long bookingId);


    /**
     * Метод возвращает объект бронирования по его id из хранилища.
     *
     * @param id
     * @return
     */
    BookingDto getBooking(long id);
}
