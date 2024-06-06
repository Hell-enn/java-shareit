package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;

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
    BookingOutcomingDto postBooking(Long userId, BookingDto bookingDto);


    /**
     * Метод обновляет объект бронирования в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param userId (идентификатор пользователя)
     * @param approved (информация о подтверждении бронирования)
     *
     * @return BookingDto
     */
    BookingOutcomingDto patchBooking(Long userId, Boolean approved, Long bookingId);


    /**
     * Метод возвращает список бронирований пользователя в состоянии state из хранилища.
     *
     * @return
     */
    List<BookingOutcomingDto> getBookings(Long userId, String state);


    /**
     * Метод удаляет бронирование с bookingId из хранилища
     */
    void deleteBooking(Long bookingId);


    /**
     * Метод возвращает объект бронирования по его id из хранилища.
     *
     * @param id
     * @return
     */
    BookingOutcomingDto getBooking(Long id, Long userId);


    /**
     * Метод возвращает список бронирований вещей пользователя в состоянии state из хранилища.
     *
     * @return
     */
    List<BookingOutcomingDto> getUserStuffBookings(Long userId, String state);

}
