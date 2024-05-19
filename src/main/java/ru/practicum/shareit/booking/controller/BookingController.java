package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingServiceImpl;


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * — userId - значение, преобразованное из заголовка запроса "X-Sharer-User-Id",
     * — bookingDto - объект,
     * и маршрутизирует в метод
     * уровня сервиса, содержащего бизнес-логику
     * приложения с целью последующего добавления в хранилище
     * объекта типа Booking.
     *
     * @param userId (идентификатор пользователя, добавляющего бронирование на вещь)
     * @param bookingDto
     * @return BookingDto
     */
    @PostMapping
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody BookingDto bookingDto) {
        return bookingServiceImpl.postBooking(userId, bookingDto);
    }


    /**
     * Эндпоинт. Метод обновляет объект бронирования в списке в случае,
     * если он в нём присутствует. Иначе выбрасывает исключение
     * с сообщением об ошибке.
     * В случае успеха возвращает обновлённый объект.
     *
     * @param userId
     * @param booking
     * @param bookingId
     * @return BookingDto
     */
    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingDto booking,
                             @PathVariable Long bookingId) {
        return bookingServiceImpl.patchBooking(userId, booking, bookingId);
    }


    /**
     * Эндпоинт. Метод возвращает список бронирований конкретного пользователя по его id.
     *
     * @param userId
     * @return List<BookingDto>
     */
    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingServiceImpl.getBookings(userId);
    }


    /**
     * Эндпоинт. Удаляет бронирование с bookingId
     *
     * @param bookingId
     */
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable(name = "id") long bookingId) {
        bookingServiceImpl.deleteBooking(bookingId);
    }


    /**
     * Эндпоинт. Метод возвращает объект бронирования по его id.
     *
     * @param id
     * @return BookingDto
     */
    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable long id) {
        return bookingServiceImpl.getBooking(id);
    }
}
