package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private UserClient userClient;
    @Autowired
    private MockMvc mvc;

    private UserDto userDto = new UserDto(
            1L,
            "Petr Petrov",
            "petrpetrov@gmail.com");

    @Test
    void testPostUserOk() throws Exception {
        when(userClient.postUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    @Test
    void testPostUserValidationException() throws Exception {
        when(userClient.postUser(any(UserDto.class)))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    void testPatchUserValidationException() throws Exception {
        when(userClient.patchUser(Mockito.anyLong(), any(UserPatchDto.class)))
                .thenThrow(ValidationException.class);

        mvc.perform(patch("/users/" + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    void testPatchUserNotFoundException() throws Exception {
        when(userClient.patchUser(Mockito.anyLong(), any(UserPatchDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/users/" + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    void testGetUsersOk() throws Exception {

        UserDto userDto1 = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");
        UserDto userDto2 = new UserDto(2L, "Ivan Ivanov", "petrpetrov@gmail.com");
        UserDto userDto3 = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");

        List<UserDto> users = List.of(userDto1, userDto2, userDto3);
        when(userClient.getUsers())
                .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[2].name", is(userDto3.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())))
                .andExpect(jsonPath("$[2].email", is(userDto3.getEmail())));
    }


    @Test
    void testGetUserOk() throws Exception {
        when(userClient.getUser(Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mvc.perform(get("/users/" + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    @Test
    void testGetUserNotFoundException() throws Exception {
        when(userClient.getUser(Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/users/" + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    void testDeleteUserOk() throws Exception {

        when(userClient.deleteUser(Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        mvc.perform(delete("/users/" + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}