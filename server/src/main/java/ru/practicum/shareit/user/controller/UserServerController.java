package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * Класс-контроллер сервера UserServerController принимает отфильтрованные
 * HTTP-запросы из шлюза, касающиеся взаимодействия с пользователями,
 * преобразует их в валидируемые объекты Java и маршрутизирует в слой
 * UserService, где содержится основная бизнес-логика по взаимодействию с объектами пользователей.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserServerController {

    private final UserService userServiceImpl;


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на добавление
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * добавления объекта типа User в хранилище.
     *
     * @param userDto (объект пользователя(dto), который необходимо добавить в хранилище).
     *
     * @return UserDto(объект опубликованного пользователя)
     */
    @PostMapping
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.debug("Принят запрос на добавление пользователя {}", userDto.getName());
        return userServiceImpl.postUser(userDto);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на обновление
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятные java объекты.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * обновления объекта типа User в хранилище.
     *
     * @param userId (объект пользователя, который необходимо добавить в хранилище),
     * @param userDto (объект пользователя(dto), который необходимо добавить в хранилище).
     *
     * @return UserDto(объект обновленного пользователя)
     */
    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable(name = "id") Long userId,
                             @RequestBody UserDto userDto) {
        log.debug("Принят запрос на обновление пользователя с id = {}", userId);
        return userServiceImpl.patchUser(userId, userDto);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на получение
     * всех объектов типа User и направляет его в текущий эндпоинт.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объектов типа User из хранилища.
     *
     * @return List<UserDto>(список всех объектов пользователей)
     */
    @GetMapping
    public List<UserDto> getUsers() {
        log.debug("Принят запрос на получение списка всех пользователей");
        return userServiceImpl.getUsers();
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на удаление
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * удаления объекта типа User из хранилища.
     *
     * @param userId (объект пользователя, который необходимо удалить из хранилища).
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(name = "id") Long userId) {
        log.debug("Принят запрос на удаление пользователя с id={}", userId);
        userServiceImpl.deleteUser(userId);
    }


    /**
     * Эндпоинт. Контроллер получает HTTP-запрос из микросервиса-шлюза на получение
     * объекта типа User и направляет его в текущий эндпоинт.
     * С помощью Spring-аннотаций метод преобразует
     * запрос в понятный java объект типа Long.
     * В рамках текущего метода происходит маршрутизация передаваемого
     * объекта в метод уровня сервиса, содержащего бизнес-логику
     * извлечения объекта типа User из хранилища.
     *
     * @param userId (объект пользователя, который необходимо получить из хранилища).
     *
     * @return UserDto(объект пользователя с userId)
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable(name = "id") Long userId) {
        log.debug("Принят запрос на получение пользователя с id={}", userId);
        return userServiceImpl.getUser(userId);
    }
}
