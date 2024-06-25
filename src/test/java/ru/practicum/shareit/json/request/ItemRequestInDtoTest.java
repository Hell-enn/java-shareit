package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestInDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestInDtoTest {
    @Autowired
    private JacksonTester<ItemRequestInDto> json;

    @Test
    public void testItemRequestInDtoSerialization() throws Exception {
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(
                1L,
                "description",
                4L);

        JsonContent<ItemRequestInDto> result = json.write(itemRequestInDto);

        assertThat(json.write(itemRequestInDto)).hasJsonPathValue("$.description", "description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(4);
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {
        String jsonItemRequestInDto = "{\"id\":\"1\",\"description\":\"description\",\"requestor\":\"4\"}";
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(
                1L,
                "description",
                4L);

        assertThat(json.parse(jsonItemRequestInDto)).usingRecursiveComparison().isEqualTo(itemRequestInDto);
    }
}
