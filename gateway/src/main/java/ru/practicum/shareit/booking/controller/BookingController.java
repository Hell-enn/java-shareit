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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Класс-контроллер шлюза BookingController принимает HTTP-запросы,
 * касающиеся взаимодействия с бронированиями вещей,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * BookingClient, где с помощью RestTemplate объект преобразуется в
 * HTTP-запрос, передаваемый в микросервис Server, где содержится основная
 * бизнес-логика по взаимодействию с объектами бронирований.
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
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, добавляющего бронирование на вещь),
     * @param bookingDto (объект бронирования).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект бронирования
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PostMapping
    public ResponseEntity<Object> postBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody @NotNull BookingDto bookingDto) {
        log.debug("Принят запрос на добавление бронирования от пользователя с id={}", userId);
        return bookingClient.postBooking(userId, bookingDto);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, одобряющего бронирование на вещь),
     * @param approved (флаг одобрения бронирования владельцем вещи),
     * @param bookingId (идентификатор бронирования, которое необходимо подтвердить или отклонить).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект обновленного бронирования
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam @NotNull Boolean approved,
                                                 @PathVariable Long bookingId) {
        log.debug("Принят запрос на " + (approved ? "подтверждение" : "отклонение") + " бронирования пользователем с id={}", userId);
        return bookingClient.patchBooking(userId, approved, bookingId);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, запрашивающего бронирование на вещь),
     * @param id (идентификатор запрашиваемого бронирования).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю объект бронирования с идентификатором id
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long id) {
        log.debug("Принят запрос на получение бронирования с id={}", id);
        return bookingClient.getBooking(userId, id);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, получающего список своих бронирований),
     * @param state (состояние бронирований, которые пользователь хочет получить),
     * @param from (позиция первого объекта бронирования в списке, с которого требуется
     *             вернуть обозначенное в size количество объектов),
     * @param size (количество объектов бронирований, которое требуется вернуть).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю список его бронирований в статусе state,
     * начиная с объекта в позиции from, в количестве size, или код ответа, отличный от 2**,
     * с описанием причины возникновения ошибки)
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
     * его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, получающего список бронирований своих вещей),
     * @param state (состояние запрашиваемых бронирований),
     * @param from (позиция первого объекта бронирования в списке, с которого требуется
     *              вернуть обозначенное в size количество объектов)
     * @param size (количество объектов бронирований, которое требуется вернуть)
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю список бронирований в статусе state
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
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
