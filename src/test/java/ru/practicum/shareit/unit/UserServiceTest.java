package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    private UserJpaRepository mockUserJpaRepository;
    private UserService userService;


    @BeforeEach
    private void create() {
        userService = new UserServiceImpl(mockUserJpaRepository);
    }


    @Test
    public void testPostUserOk() {
        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDto userDto = new UserDto(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserDto addedUserDto = userService.postUser(userDto);
        Assertions.assertEquals(userDto, addedUserDto);
    }


    @Test
    public void testPostUserNull() {
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any(User.class)))
                .thenThrow(new ValidationException("Вы не передали информацию о пользователе!"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.postUser(null));

        Assertions.assertEquals("Вы не передали информацию о пользователе!", exception.getMessage());
    }


    @Test
    public void testPostUserWithoutEmail() {
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any(User.class)))
                .thenThrow(new ValidationException("Вы не передали информацию об электронной почте пользователя!"));

        UserDto userDto = new UserDto(null, "Ivan Ivanov", null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.postUser(userDto));

        Assertions.assertEquals("Вы не передали информацию об электронной почте пользователя!", exception.getMessage());
    }


    @Test
    public void testPatchUserOk() {
        User addedUser = new User(1L, "Ivan Ivanov", "ivanivanov1@gmail.com");
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any(User.class)))
                .thenReturn(addedUser);
        Mockito
                .when(mockUserJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(addedUser));

        UserDto userDto = new UserDto(1L, "Ivan Ivanov", "ivanivanov1@gmail.com");
        UserDto addedUserDto = userService.patchUser(1L, userDto);
        Assertions.assertEquals(userDto, addedUserDto);
    }


    @Test
    public void testPatchUserNull() {
        Mockito
                .when(mockUserJpaRepository.save(Mockito.any(User.class)))
                .thenThrow(new ValidationException("Вы не передали информацию об электронной почте пользователя!"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.patchUser(1L, null));

        Assertions.assertEquals("Вы не передали информацию о пользователе!", exception.getMessage());
    }


    @Test
    public void testPatchUserNotFound() {
        Mockito
                .when(mockUserJpaRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с таким id не найден!"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.patchUser(1L, new UserDto(1L, "...", "aaa@aaa.com")));

        Assertions.assertEquals("Пользователь с таким id не найден!", exception.getMessage());
    }


    @Test
    public void testGetUsersEmpty() {
        List<User> users = new ArrayList<>();
        Mockito
                .when(mockUserJpaRepository.findAll())
                .thenReturn(users);

        Assertions.assertEquals(new ArrayList<>(), mockUserJpaRepository.findAll());
    }


    @Test
    public void testGetUsers() {
        User user1 = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User user2 = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User user3 = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        List<User> users = List.of(user1, user2, user3);
        Mockito
                .when(mockUserJpaRepository.findAll())
                .thenReturn(users);

        Assertions.assertEquals(List.of(user1, user2, user3), mockUserJpaRepository.findAll());
    }


    @Test
    public void testGetUserNotFound() {
        Mockito
                .when(mockUserJpaRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден!"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(1L));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void testGetUserOk() {
        UserDto userDto = new UserDto(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        Mockito
                .when(mockUserJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Assertions.assertEquals(userService.getUser(1L), userDto);
    }
}
