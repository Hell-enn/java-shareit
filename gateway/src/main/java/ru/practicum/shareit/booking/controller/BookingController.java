package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс-контроллер BookingController принимает HTTP-запросы,
 * касающиеся взаимодействия с бронированиями вещей,
 * преобразует их в объекты Java и маршрутизирует в сервисный слой
 * BookingService для последующего взаимодействия с объектам-вещами
 * из слоя-репозитория BookingRepository.
 */
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;


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
    public ResponseEntity<Object> postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Принят запрос на добавление бронирования от пользователя с id={}", userId);
        return bookingClient.postBooking(userId, bookingDto);
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
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam Boolean approved,
                                     @PathVariable Long bookingId) {
        log.debug("Принят запрос на " + (approved ? "подтверждение" : "отклонение") + " бронирования пользователем с id={}", userId);
        return bookingClient.patchBooking(userId, approved, bookingId);
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
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long id) {
        log.debug("Принят запрос на получение бронирования с id={}", id);
        return bookingClient.getBooking(userId, id);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java объекты:
     * @param userId (идентификатор пользователя, получающего список своих бронирований),
     *
     * В рамках эндпоинта происходит маршрутизация на
     * уровень сервиса, содержащего бизнес-логику
     * приложения с целью последующего извлечения из репозитория
     * списка объектов типа Booking, принадлежащих пользователю с идентификатором userID.
     *
     * @return List<BookingOutcomingDto> (возвращаемый пользователю список бронирований в статусе state)
     */
    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.debug("Принят запрос на получение списка всех бронирований");
        return bookingClient.getBookings(userId, state, from, size);
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
    public ResponseEntity<Object> getUserStuffBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.debug("Принят запрос на получение списка всех бронирований");
        return bookingClient.getUserStuffBookings(userId, state, from, size);
    }
}
