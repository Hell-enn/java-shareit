package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

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
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public UserDto postUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User addedUser;
        try {
            addedUser = userJpaRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            log.debug("При добавлении пользователя {} возникла ошибка на стороне базы данных", userDto.getName());
            throw new DataIntegrityViolationException("Ошибка при добавлении пользователя!");
        }
        return userMapper.toUserDto(addedUser);
    }


    @Override
    public UserDto patchUser(Long userId, UserDto userDto) {
        User addedUser = userJpaRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден!"));
        userMapper.updateUserFromDto(userDto, addedUser);
        userJpaRepository.save(addedUser);
        log.debug("Пользователь \"{}\" обновлён!", addedUser.getName());
        return userMapper.toUserDto(addedUser);
    }


    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers() {
        List<User> users = userJpaRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        users.forEach(user -> userDtos.add(userMapper.toUserDto(user)));
        log.debug("Возвращаем список пользователей в количестве {}", userDtos.size());
        return userDtos;
    }


    @Override
    public Long deleteUser(Long userId) {
        log.debug("Удаляем пользователя с id={}", userId);
        userJpaRepository.deleteById(userId);
        return userId;
    }


    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long userId) {
       User addedUserOpt = userJpaRepository.findById(userId).orElseThrow(
               () -> new NotFoundException("Пользователь не найден!"));
       log.debug("Возвращаем пользователя с id={}", userId);
        return userMapper.toUserDto(addedUserOpt);
    }
}