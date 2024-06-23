package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.validation.Valid;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemResponseDtoTest {
    @Autowired
    private JacksonTester<ItemResponseDto> json;
    @Valid
    private ItemResponseDto itemResponseDto;

    @Test
    public void testItemResponseDtoSerialization() throws Exception {

        itemResponseDto = new ItemResponseDto(
                1L,
                "name",
                "description",
                1L,
                true);

        JsonContent<ItemResponseDto> result = json.write(itemResponseDto);

        assertThat(json.write(itemResponseDto)).hasJsonPathValue("$.description", "description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {

        itemResponseDto = new ItemResponseDto(
                1L,
                "name",
                "description",
                1L,
                true);

        String jsonItemResponseDto =
                        "{\"id\":\"1\"," +
                        "\"name\":\"name\"," +
                        "\"description\":\"description\"," +
                        "\"available\":\"true\"," +
                        "\"requestId\":\"1\"}";

        assertThat(json.parse(jsonItemResponseDto)).usingRecursiveComparison().isEqualTo(itemResponseDto);
    }

    @Test
    public void testItemRequestInDtoWithoutRequester() throws Exception {

        itemResponseDto = new ItemResponseDto(
                1L,
                "",
                "",
                1L,
                true);

        JsonContent<ItemResponseDto> result = json.write(itemResponseDto);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("");
    }
}
