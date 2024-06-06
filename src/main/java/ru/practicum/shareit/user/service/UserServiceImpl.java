package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс UserService предоставляет функциональность по
 * взаимодействию со списком пользователей (бизнес-логика) -
 * объекты типа User
 * (добавление, удаление, вывод списка пользователей).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public UserDto postUser(UserDto userDto) {
        validateNewUser(userDto);
        User user = userMapper.toUser(userDto);
        User addedUser;
        try {
            addedUser = userJpaRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AlreadyExistsException("Ошибка при добавлении пользователя!");
        }
        return userMapper.toUserDto(addedUser);
    }


    @Override
    public UserDto patchUser(Long userId, UserDto userDto) {
        validateUpdateUser(userId, userDto);
        Optional<User> addedUserOpt = userJpaRepository.findById(userId);
        User addedUser = null;
        if (addedUserOpt.isPresent())
            addedUser = addedUserOpt.get();
        userMapper.updateUserFromDto(userDto, addedUser);
        userJpaRepository.save(addedUser);
        log.debug("Пользователь \"{}\" обновлён!", addedUser.getName());
        return userMapper.toUserDto(addedUser);
    }


    @Override
    public List<UserDto> getUsers() {
        List<User> users = userJpaRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        users.forEach(user -> userDtos.add(userMapper.toUserDto(user)));
        return userDtos;
    }


    @Override
    public void deleteUser(Long userId) {
        userJpaRepository.deleteById(userId);
    }


    @Override
    public UserDto getUser(Long userId) {
       Optional<User> addedUserOpt = userJpaRepository.findById(userId);
       if (addedUserOpt.isEmpty())
           throw new NotFoundException("Пользователь не найден!");
        return userMapper.toUserDto(addedUserOpt.get());
    }


    /**
     * Закрытый служебный метод проверяет объект типа User
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа UserDto при попытке его добавления
     * в хранилище.
     * В случае неудачи выбрасывает исключение с сообщением об ошибке.
     *
     * @param userDto (объект пользователя)
     */
    private void validateNewUser(UserDto userDto) {

        String message = "";

        if (userDto == null)
            message = "Вы не передали информацию о пользователе!";
        else if (userDto.getEmail() == null)
            message = "Вы не передали информацию об электронной почте пользователя!";
        if (!message.isBlank()) {
            throw new ValidationException(message);
        }
    }


    /**
     * Закрытый служебный метод проверяет объект типа User
     * на соответствие ряду условий. Используется впоследствии
     * для валидации объекта типа UserDto при попытке обновления хранимого
     * в репозитории объекта.
     * В случае неудачи выбрасывает исключение с сообщением об ошибке.
     *
     * @param userId (идентификатор пользователя)
     * @param userDto (объект пользователя)
     */
    private void validateUpdateUser(Long userId, UserDto userDto) {
        if (userDto == null)
            throw new ValidationException("Вы не передали информацию о пользователе!");
        Optional<User> userOpt = userJpaRepository.findById(userId);
        if (userOpt.isEmpty())
            throw new NotFoundException("Пользователь с таким id не найден!");

        for (User user: userJpaRepository.findAll()) {
            if (user != null && user.getEmail().equals(userDto.getEmail()) && !user.getId().equals(userId)) {
                throw new RuntimeException("Пользователь с такой почтой уже существует!");
            }
        }
    }
}
