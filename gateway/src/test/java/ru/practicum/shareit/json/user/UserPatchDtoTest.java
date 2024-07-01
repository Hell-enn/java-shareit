package ru.practicum.shareit.json.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserPatchDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserPatchDtoTest {
    @Autowired
    private JacksonTester<UserPatchDto> json;

    @Test
    public void testUserDtoSerialization() throws Exception {
        UserPatchDto userDto = new UserPatchDto(
                "Petr Petrov",
                "petrpetrov@gmail.com");

        JsonContent<UserPatchDto> result = json.write(userDto);

        assertThat(json.write(userDto)).hasJsonPathValue("$.name", "Petr Petrov");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Petr Petrov");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("petrpetrov@gmail.com");
    }

    @Test
    public void testUserDtoDeserialization() throws Exception {
        String jsonUserDto = "{\"id\":\"1\",\"name\":\"Petr Petrov\",\"email\":\"petrpetrov@gmail.com\"}";
        UserPatchDto userDto = new UserPatchDto(
                "Petr Petrov",
                "petrpetrov@gmail.com");

        assertThat(json.parse(jsonUserDto)).usingRecursiveComparison().isEqualTo(userDto);
    }
}