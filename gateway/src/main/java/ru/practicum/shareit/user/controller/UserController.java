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

/**
 * Класс-контроллер UserController принимает HTTP-запросы,
 * касающиеся взаимодействия с пользователями,
 * преобразует их в объекты Java и маршрутизирует в сервисный слой
 * UserService для последующего взаимодействия с объектам-пользователями
 * из хранилища UserDao.
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
     * Эндпоинт. Контроллер получает HTTP-запрос на добавление
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * добавления объекта типа User в хранилище.
     *
     * @param userPostDto (объект пользователя(dto), который необходимо добавить в хранилище)
     *
     * @return User
     */
    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto userPostDto) {
        log.debug("Принят запрос на добавление пользователя {}", userPostDto.getName());
        return userClient.postUser(userPostDto);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на обновление
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * обновления объекта типа User в хранилище.
     *
     * @param userId (объект пользователя, который необходимо добавить в хранилище)
     * @param userPatchDto (объект пользователя(dto), который необходимо добавить в хранилище)
     *
     * @return UserDto
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable(name = "id") Long userId,
                             @Valid @RequestBody UserPatchDto userPatchDto) {
        log.debug("Принят запрос на обновление пользователя с id = {}", userId);
        return userClient.patchUser(userId, userPatchDto);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на получение
     * всех объектов типа User и направляет его в текущий эндпоинт.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объектов типа User из хранилища.
     *
     * @return List<UserDto>
     */
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("Принят запрос на получение списка всех пользователей");
        return userClient.getUsers();
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на удаление
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * удаления объекта типа User из хранилища.
     *
     * @param userId (объект пользователя, который необходимо удалить из хранилища)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long userId) {
        log.debug("Принят запрос на удаление пользователя с id={}", userId);
        userClient.deleteUser(userId);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос на получение
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объекта типа User из хранилища.
     *
     * @param userId (объект пользователя, который необходимо удалить из хранилища)
     *
     * @return UserDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(name = "id") Long userId) {
        log.debug("Принят запрос на получение пользователя с id={}", userId);
        return userClient.getUser(userId);
    }
}