package ru.practicum.shareit.unit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.unit.entities.UserTest;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserClientTest {
    @Mock
    private RestTemplate rest;
    private UserClient userClient;
    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    @BeforeEach
    public void create() {
        userClient = new UserClient("http://localhost:8080", restTemplateBuilder);
    }
/*
    @Test
    public void testPostUserOk() {
        UserTest user = new UserTest(1L, "Ivan Ivanov", "ivanivanov@gmail.com");

        when(rest.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(Class.class), Mockito.anyList()))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));
        when(rest.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.any(Class.class)))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        UserDto userDto = new UserDto(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ResponseEntity<Object> responseEntityUserDto = new ResponseEntity<>(userDto, HttpStatus.OK);
        ResponseEntity<Object> addedUserDto = userClient.postUser(userDto);
        Assertions.assertEquals(responseEntityUserDto, addedUserDto);
    }


    @Test
    public void testPostUserNull() {
        when(userClient.post(Mockito.anyString(), Mockito.any(UserTest.class)))
                .thenThrow(new ValidationException("Вы не передали информацию о пользователе!"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userClient.postUser(null));

        Assertions.assertEquals("Вы не передали информацию о пользователе!", exception.getMessage());
    }


    @Test
    public void testPostUserWithoutEmail() {
        when(userClient.post(Mockito.anyString(), Mockito.any(UserTest.class)))
                .thenThrow(new ValidationException("Вы не передали информацию об электронной почте пользователя!"));

        UserDto userDto = new UserDto(null, "Ivan Ivanov", null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userClient.postUser(userDto));

        Assertions.assertEquals("Вы не передали информацию об электронной почте пользователя!", exception.getMessage());
    }


    @Test
    public void testPostUserDataIntegrityException() {
        when(userClient.post(Mockito.anyString(), Mockito.any(UserTest.class)))
                .thenThrow(new AlreadyExistsException("Ошибка при добавлении пользователя!"));

        UserDto userDto = new UserDto(null, "Ivan Ivanov", "email@mail.com");

        final AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> userClient.postUser(userDto));

        Assertions.assertEquals("Ошибка при добавлении пользователя!", exception.getMessage());
    }


    @Test
    public void testPatchUserOk() {
        UserTest addedUser = new UserTest(1L, "Ivan Ivanov", "ivanivanov1@gmail.com");
        when(userClient.patch(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(addedUser, HttpStatus.OK));

        UserPatchDto userDto = new UserPatchDto("Ivan Ivanov", "ivanivanov1@gmail.com");
        ResponseEntity<Object> responseEntityUserDto = new ResponseEntity<>(userDto, HttpStatus.OK);
        ResponseEntity<Object> addedUserDto = userClient.patchUser(1L, userDto);
        Assertions.assertEquals(responseEntityUserDto, addedUserDto);
    }


    @Test
    public void testPatchUserNull() {
        when(userClient.patch(Mockito.anyString(), Mockito.anyLong()))
                .thenThrow(new ValidationException("Вы не передали информацию об электронной почте пользователя!"));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userClient.patchUser(1L, null));

        Assertions.assertEquals("Вы не передали информацию о пользователе!", exception.getMessage());
    }


    @Test
    public void testPatchUserNotFound() {
        when(userClient.patch(Mockito.anyString(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с таким id не найден!"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userClient.patchUser(1L, new UserPatchDto("...", "aaa@aaa.com")));

        Assertions.assertEquals("Пользователь с таким id не найден!", exception.getMessage());
    }


    @Test
    public void testGetUsersEmpty() {
        List<UserTest> users = new ArrayList<>();
        when(userClient.get(Mockito.anyString()))
                .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));

        Assertions.assertEquals(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK), userClient.getUsers());
    }


    @Test
    public void testGetUsers() {
        UserTest user1 = new UserTest(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserTest user2 = new UserTest(2L, "Petr Petrov", "petrpetrov@gmail.com");
        UserTest user3 = new UserTest(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        List<UserTest> users = List.of(user1, user2, user3);
        when(userClient.get(Mockito.anyString()))
                .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));

        UserDto userDto1 = new UserDto(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserDto userDto2 = new UserDto(2L, "Petr Petrov", "petrpetrov@gmail.com");
        UserDto userDto3 = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");

        List<UserDto> userDtos = List.of(userDto1, userDto2, userDto3);
        Assertions.assertEquals(new ResponseEntity<>(userDtos, HttpStatus.OK), userClient.getUsers());
    }


    @Test
    public void testGetUserNotFound() {
        when(userClient.get(Mockito.anyString(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден!"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userClient.getUser(1L));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void testGetUserOk() {
        UserDto userDto = new UserDto(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserTest user = new UserTest(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        when(userClient.get(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        Assertions.assertEquals(userClient.getUser(1L), new ResponseEntity<>(userDto, HttpStatus.OK));
    }


    @Test
    public void testDeleteUserOk() {
        when(userClient.delete(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Assertions.assertEquals(userClient.deleteUser(1L), new ResponseEntity<>(HttpStatus.OK));
    }
 */
}