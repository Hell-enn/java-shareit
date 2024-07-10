package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestInDtoTest {
    @Autowired
    private JacksonTester<ItemRequestPostDto> json;

    @Test
    public void testItemRequestInDtoSerialization() throws Exception {
        ItemRequestPostDto itemRequestInDto = new ItemRequestPostDto(
                1L,
                "description",
                4L);

        JsonContent<ItemRequestPostDto> result = json.write(itemRequestInDto);

        assertThat(json.write(itemRequestInDto)).hasJsonPathValue("$.description", "description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(4);
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {
        String jsonItemRequestInDto = "{\"id\":\"1\",\"description\":\"description\",\"requestor\":\"4\"}";
        ItemRequestPostDto itemRequestInDto = new ItemRequestPostDto(
                1L,
                "description",
                4L);

        assertThat(json.parse(jsonItemRequestInDto)).usingRecursiveComparison().isEqualTo(itemRequestInDto);
    }
}