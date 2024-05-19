package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

/**
 * Интерфейс-хранилище UserDao определяет контракт,
 * включающий сигнатуры ряда методов, которые реализуют
 * расширяющие его классы слоя манипуляции данными на уровне хранилища
 * в части работы с бронированиями приложения ShareIt.
 */
public interface BookingDao {

    /**
     * Преобразует объект типа BookingDto в Booking и добавляет его в хранилище.
     *
     * @param userId (идентификатор пользователя, отправившего запрос на добавление бронирования)
     * @param bookingDto (объект, содержащий информацию о новом пользователе)
     *
     * @return Booking
     */
    Booking addBooking(Long userId, BookingDto bookingDto);


    /**
     * Обновляет объект типа Booking, уже содержащийся в хранилище,
     * значениями, привнесёнными объектом типа BookingDto, переданным в качестве параметра.
     *
     * @param userId (идентификатор пользователя, отправившего запрос на обновление бронирования)
     * @param bookingDto (объект, содержащий обновленную информацию о пользователе)
     * @param bookingId (идентификатор бронирования, информацию о котором нужно обновить в хранилище)
     *
     * @return Booking
     */
    Booking updateBooking(Long userId, BookingDto bookingDto, Long bookingId);


    /**
     * Удаляет объект типа Booking с id из списка.
     * @param id
     */
    void deleteBooking(Long id);


    /**
     * Метод возвращает из хранилища объект бронирования по его id.
     * @param id
     * @return
     */
    Booking getBooking(Long id);


    /**
     * Метод отвечает на вопрос, содержится ли бронирование с
     * данным id в списке.
     * @return
     */
    boolean containsBooking(Long id);


    /**
     * Метод возвращает список бронирований.
     * @return
     */
    List<Booking> getBookings(Long userId);
}
