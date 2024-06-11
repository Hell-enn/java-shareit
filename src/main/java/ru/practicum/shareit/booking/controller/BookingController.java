package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * Класс-контроллер BookingController принимает HTTP-запросы,
 * касающиеся взаимодействия с бронированиями вещей,
 * преобразует их в объекты Java и маршрутизирует в сервисный слой
 * BookingService для последующего взаимодействия с объектам-вещами
 * из слоя-репозитория BookingRepository.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingServiceImpl;


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * @param userId (идентификатор пользователя, добавляющего бронирование на вещь),
     * @param bookingDto (объект самой вещи).
     *
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего добавления в хранилище
     * объекта типа Booking.
     *
     * @return BookingOutcomingDto (возвращаемый пользователю объект бронирования)
     */
    @PostMapping
    public BookingOutcomingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Принят запрос на добавление бронирования от пользователя с id={}", userId);
        return bookingServiceImpl.postBooking(userId, bookingDto);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * @param userId (идентификатор пользователя, одобряющего бронирование на вещь),
     * @param approved (флаг одобрения бронирования владельцем вещи),
     * @param bookingId (идентификатор бронирования, которое необходимо подтвердить или отклонить).
     *
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего обновления в хранилище
     * объекта типа Booking.
     *
     * @return BookingOutcomingDto (возвращаемый пользователю объект обновленного бронирования)
     */
    @PatchMapping("/{bookingId}")
    public BookingOutcomingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam Boolean approved,
                                     @PathVariable Long bookingId) {
        return bookingServiceImpl.patchBooking(userId, approved, bookingId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * @param userId (идентификатор пользователя, запрашивающего бронирование на вещь),
     * @param id (идентификатор запрашиваемого бронирования).
     *
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего получения из репозитория
     * объекта типа Booking.
     *
     * @return BookingOutcomingDto (возвращаемый пользователю объект бронирования с идентификатором id)
     */
    @GetMapping("/{id}")
    public BookingOutcomingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long id) {
        log.debug("Принят запрос на получение бронирования с id={}", id);
        return bookingServiceImpl.getBooking(id, userId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * @param userId (идентификатор пользователя, получающего список своих бронирований),
     * @param state (состояние запрашиваемых бронирований).
     *
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего извлечения из репозитория
     * списка объектов типа Booking, принадлежащих пользователю с идентификатором userID.
     *
     * @return List<BookingOutcomingDto> (возвращаемый пользователю список бронирований в статусе state)
     */
    @GetMapping
    public List<BookingOutcomingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        log.debug("Принят запрос на получение списка всех бронирований");
        return bookingServiceImpl.getBookings(userId, state);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * @param userId (идентификатор пользователя, получающего список бронирований своих вещей),
     * @param state (состояние запрашиваемых бронирований).
     *
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего извлечения из репозитория
     * списка объектов типа Booking, относящихся к принадлежащим пользователю с идентификатором userID вещам.
     *
     * @return List<BookingOutcomingDto> (возвращаемый пользователю список бронирований в статусе state)
     */
    @GetMapping("/owner")
    public List<BookingOutcomingDto> getUserStuffBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        log.debug("Принят запрос на получение списка всех бронирований");
        return bookingServiceImpl.getUserStuffBookings(userId, state);
    }


    /**
     * Эндпоинт. Удаляет бронирование с bookingId
     *
     * @param bookingId
     */
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable(name = "id") Long bookingId) {
        log.debug("Принят запрос на удаление бронирования с id={}", bookingId);
        bookingServiceImpl.deleteBooking(bookingId);
    }
}
