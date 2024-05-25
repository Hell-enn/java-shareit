package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Класс InMemoryUserDao - реализация интерфейса UserDao.
 * Предоставляет хранение данных о пользователях приложения
 * только во время выполнения силами структуры данных HashMap.
 */
@Component
@Slf4j
public class InMemoryUserDao implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private long id;

    public InMemoryUserDao() {
    }


    /**
     * Закрытый служебный метод генерирует и возвращает идентификатор пользователя.
     * @return Long
     */
    private long getId() {
        return ++id;
    }


    @Override
    public User addUser(UserDto userDto) {

        if (users.get(userDto.getId()) != null)
            throw new AlreadyExistsException("Данный пользователь уже зарегистрирован!");

        for (User addedUser: users.values()) {
            if (addedUser.getEmail().equals(userDto.getEmail()))
                throw new AlreadyExistsException("Пользователь с такой электронной почтой уже существует!");
        }

        User newUser = UserMapper.toUser(userDto, getId());

        users.put(newUser.getId(), newUser);

        log.debug("Пользователь {} добавлен!", newUser.getName());
        return newUser;
    }


    @Override
    public User updateUser(Long id, UserDto user) {

        User addedUser = users.get(id);

        if (users.get(id) == null)
            throw new NotFoundException("Пользователь с id=" + id + " не найден!");

        if (user.getName() != null && !user.getName().isBlank() && !addedUser.getName().equals(user.getName()))
            addedUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().isBlank() && !addedUser.getEmail().equals(user.getEmail())) {

            for (User currentUser: users.values()) {
                if (currentUser.getEmail().equals(user.getEmail()))
                    throw new AlreadyExistsException("Пользователь с такой почтой уже зарегистрирован!");
            }
            addedUser.setEmail(user.getEmail());
        }

        users.put(addedUser.getId(), addedUser);
        log.debug("Пользователь \"{}\" обновлён!", addedUser.getName());
        return addedUser;
    }


    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя по id: {}", id);
        users.remove(id);
    }


    @Override
    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null)
            throw new NotFoundException("Пользователь с id=" + id + " не найден!");

        return user;
    }


    @Override
    public boolean containsUser(Long id) {
        return users.containsKey(id);
    }


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}