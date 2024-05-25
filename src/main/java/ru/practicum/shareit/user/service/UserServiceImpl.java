package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс UserService предоставляет функциональность по
 * взаимодействию со списком пользователей (бизнес-логика) -
 * объекты типа User
 * (добавление, удаление, вывод списка пользователей).
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao inMemoryUserDao;


    @Override
    public UserDto postUser(UserDto userDto) {
        validateUser(userDto);
        return UserMapper.toUserDto(inMemoryUserDao.addUser(userDto));
    }


    @Override
    public UserDto patchUser(Long userId, UserDto userDto) {
        return UserMapper.toUserDto(inMemoryUserDao.updateUser(userId, userDto));
    }


    @Override
    public List<UserDto> getUsers() {
        List<User> users = inMemoryUserDao.getUsers();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user: users) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        return userDtoList;
    }


    @Override
    public void deleteUser(Long userId) {
        inMemoryUserDao.deleteUser(userId);
    }


    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(inMemoryUserDao.getUser(userId));
    }


    /**
     * Закрытый служебный метод проверяет объект типа User
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа UserDto при попытке его добавления
     * в хранилище.
     * В случае неудачи выбрасывает исключение ValidationException
     * с сообщением об ошибке.
     *
     * @param userDto (объект пользователя, передаваемый с помощью HTTP-запроса)
     */
    private void validateUser(UserDto userDto) {

        String message = "";

        if (userDto == null)
            message = "Вы не передали информацию о пользователе!";
        else if (userDto.getEmail() == null)
            message = "Вы не передали информацию об электронной почте пользователя!";

        if (!message.isBlank()) {
            throw new ValidationException(message);
        }

    }
}
