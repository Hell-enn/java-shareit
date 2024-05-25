package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Интерфейс-хранилище UserDao определяет контракт,
 * включающий сигнатуры ряда методов, которые реализуют
 * расширяющие его классы слоя манипуляции данными на уровне хранилища
 * в части работы с пользователями приложения ShareIt.
 */
public interface UserDao {

    /**
     * Преобразует объект типа UserDto в User и добавляет его в хранилище.
     *
     * @param userDto (объект, содержащий информацию о новом пользователе)
     *
     * @return User
     */
    User addUser(UserDto userDto);


    /**
     * Обновляет объект типа User, уже содержащийся в хранилище,
     * значениями, привнесёнными объектом типа UserDto, переданным в качестве параметра.
     *
     * @param userId (идентификатор пользователя, информацию о котором нужно обновить в хранилище)
     * @param userDto (объект, содержащий обновленную информацию о пользователе)
     *
     * @return User
     */
    User updateUser(Long userId, UserDto userDto);


    /**
     * Удаляет объект типа User с идентификатором userId из хранилища.
     *
     * @param userId (идентификатор пользователя)
     */
    void deleteUser(Long userId);


    /**
     * Возвращает из хранилища объект типа User по его идентификатору userId.
     *
     * @param userId (идентификатор пользователя)
     *
     * @return User
     */
    User getUser(Long userId);


    /**
     * Метод отвечает на вопрос, содержится ли объект типа User с
     * идентификатором userId в хранилище.
     *
     * @param userId (идентификатор пользователя)
     *
     * @return boolean
     */
    boolean containsUser(Long userId);


    /**
     * Метод возвращает список, содержащий все объекты типа User из хранилища.
     *
     * @return List<User>
     */
    List<User> getUsers();
}