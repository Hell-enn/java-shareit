package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * Класс-контроллер сервера BookingServerController принимает отфильтрованные
 * HTTP-запросы из шлюза, касающиеся взаимодействия с бронированиями вещей,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * BookingService, где содержится основная бизнес-логика по взаимодействию с объектами бронирований.
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingServerController {

    private final BookingService bookingServiceImpl;

    /**
     * Эндпоинт. Метод получает запрос из микросервиса-шлюза,
     * парсит его в понятные java объекты:
     * @param userId (идентификатор пользователя, добавляющего бронирование на вещь),
     * @param bookingDto (объект самой вещи).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего добавления в хранилище
     * объекта типа Booking.
     *
     * @return BookingOutcomingDto (возвращаемый пользователю объект бронирования)
     */
    @PostMapping
    public BookingOutcomingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody BookingDto bookingDto) {
        log.debug("Принят запрос на добавление бронирования от пользователя с id={}", userId);
        return bookingServiceImpl.postBooking(userId, bookingDto);
    }


    /**
     * Эндпоинт. Метод получает запрос из микросервиса-шлюза,
     * парсит его в понятные java объекты:
     * @param userId (идентификатор пользователя, одобряющего бронирование на вещь),
     * @param approved (флаг одобрения бронирования владельцем вещи),
     * @param bookingId (идентификатор бронирования, которое необходимо подтвердить или отклонить).
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
        log.debug("Принят запрос на " + (approved ? "подтверждение" : "отклонение") + " бронирования пользователем с id={}", userId);
        return bookingServiceImpl.patchBooking(userId, approved, bookingId);
    }


    /**
     * Эндпоинт. Метод получает запрос из микросервиса-шлюза,
     * парсит его в понятные java объекты:
     * @param userId (идентификатор пользователя, запрашивающего бронирование на вещь),
     * @param id (идентификатор запрашиваемого бронирования).
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
     * Эндпоинт. Метод получает запрос из микросервиса-шлюза,
     * парсит его в понятные java объекты:
     * @param userId (идентификатор пользователя, получающего список своих бронирований),
     * @param state (состояние бронирований, которые пользователь хочет получить),
     * @param from (позиция первого объекта бронирования в списке, с которого требуется
     *             вернуть обозначенное в size количество объектов),
     * @param size (количество объектов бронирований, которое требуется вернуть).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего извлечения из репозитория
     * списка объектов типа Booking, принадлежащих пользователю с идентификатором userID.
     *
     * @return List<BookingOutcomingDto> (возвращаемый пользователю список бронирований в статусе state)
     */
    @GetMapping
    public List<BookingOutcomingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size) {
        log.debug("Принят запрос на получение списка всех бронирований");
        return bookingServiceImpl.getBookings(userId, state, from, size);
    }


    /**
     * Эндпоинт. Метод получает запрос из микросервиса-шлюза,
     * парсит его в понятные java объекты:
     * @param userId (идентификатор пользователя, получающего список бронирований своих вещей),
     * @param state (состояние бронирований, которые пользователь хочет получить),
     * @param from (позиция первого объекта бронирования в списке, с которого требуется
     *             вернуть обозначенное в size количество объектов),
     * @param size (количество объектов бронирований, которое требуется вернуть).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего извлечения из репозитория
     * списка объектов типа Booking, относящихся к принадлежащим пользователю с идентификатором userID вещам.
     *
     * @return List<BookingOutcomingDto> (возвращаемый пользователю список бронирований в статусе state)
     */
    @GetMapping("/owner")
    public List<BookingOutcomingDto> getUserStuffBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam String state,
                                                          @RequestParam Integer from,
                                                          @RequestParam Integer size) {
        log.debug("Принят запрос на получение списка всех бронирований");
        return bookingServiceImpl.getUserStuffBookings(userId, state, from, size);
    }
}
