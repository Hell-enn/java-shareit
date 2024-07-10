package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Класс-контроллер шлюза UserController принимает HTTP-запросы,
 * касающиеся взаимодействия с пользователями приложения,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * BookingClient, где с помощью RestTemplate объект преобразуется в
 * HTTP-запрос, передаваемый в микросервис Server, где содержится основная
 * бизнес-логика по взаимодействию с объектами бронирований.
 */
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    @Autowired
    private final UserClient userClient;


    /**
     * Эндпоинт. Метод получает запрос на публикацию пользователя, парсит
     * его в понятные java, валидируемые объекты:
     * @param userPostDto (объект, содержащий информацию о новом пользователе приложения).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый объект с информацией о пользователе
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody @NotNull UserDto userPostDto) {
        log.debug("Принят запрос на добавление пользователя {}", userPostDto.getName());
        return userClient.postUser(userPostDto);
    }


    /**
     * Эндпоинт. Метод получает запрос пользователя на обновление информации о себе,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, вносящего изменения в информацию о себе),
     * @param userPatchDto (объект, содержащий обновленную информацию о пользователе),
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый объект обновленного пользователя
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable(name = "id") Long userId,
                                            @Valid @RequestBody @NotNull UserPatchDto userPatchDto) {
        log.debug("Принят запрос на обновление пользователя с id = {}", userId);
        return userClient.patchUser(userId, userPatchDto);
    }


    /**
     * Эндпоинт. Метод получает запрос на получения списка объектов с информацией о пользователях,
     * парсит его в понятные java, валидируемые объекты:
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый список объектов с информацией о пользователях,
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("Принят запрос на получение списка всех пользователей");
        return userClient.getUsers();
    }


    /**
     * Эндпоинт. Метод получает запрос на удаление пользователя,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, информацию о котором необходимо удалить).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый пользователю код ответа 2** в случае успеха
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long userId) {
        log.debug("Принят запрос на удаление пользователя с id={}", userId);
        userClient.deleteUser(userId);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }


    /**
     * Эндпоинт. Метод получает запрос на получение информации о пользователе с id,
     * парсит его в понятные java, валидируемые объекты:
     * @param userId (идентификатор пользователя, информацию о котором необходимо получить).
     * В рамках эндпоинта происходит маршрутизация на
     * уровень клиента взаимодействия с микросервисом Server.
     *
     * @return ResponseEntity<Object> (возвращаемый объект пользователя с идентификатором id
     * или код ответа, отличный от 2**, с описанием причины возникновения ошибки)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(name = "id") Long userId) {
        log.debug("Принят запрос на получение пользователя с id={}", userId);
        return userClient.getUser(userId);
    }
}