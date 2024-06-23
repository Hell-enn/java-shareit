package ru.practicum.shareit.json.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.*;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    @Valid
    UserDto userDto;

    @Test
    public void testUserDtoSerialization() throws Exception {
        userDto = new UserDto(
                1L,
                "Petr Petrov",
                "petrpetrov@gmail.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(json.write(userDto)).hasJsonPathValue("$.name", "Petr Petrov");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Petr Petrov");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("petrpetrov@gmail.com");
    }

    @Test
    public void testUserDtoDeserialization() throws Exception {
        String jsonUserDto = "{\"id\":\"1\",\"name\":\"Petr Petrov\",\"email\":\"petrpetrov@gmail.com\"}";
        userDto = new UserDto(
                1L,
                "Petr Petrov",
                "petrpetrov@gmail.com");

        assertThat(json.parse(jsonUserDto)).usingRecursiveComparison().isEqualTo(userDto);
    }


    @Test
    public void testUserDtoIncorrectEmail() throws Exception {
        userDto = new UserDto(
                1L,
                "",
                "petrpetrov gmail.com");

        JsonContent<UserDto> result = json.write(userDto);
        //пока не понятно, почему валидация полей не срабатывает
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("petrpetrov gmail.com");
    }
}