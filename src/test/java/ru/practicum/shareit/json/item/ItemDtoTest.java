package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    @Valid
    private ItemDto itemDto;

    @Test
    public void testItemDtoSerialization() throws Exception {
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                1L,
                List.of("comment1", "comment2", "comment3"));

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(json.write(itemDto)).hasJsonPathValue("$.description", "description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(List.of("comment1", "comment2", "comment3"));
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {
        String jsonItemDto =
                        "{\"id\":\"1\"," +
                        "\"name\":\"name\"," +
                        "\"description\":\"description\"," +
                        "\"available\":\"true\"," +
                        "\"owner\":\"1\"," +
                        "\"requestId\":\"1\"," +
                        "\"comments\":[\"comment1\",\"comment2\",\"comment3\"]}";
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                1L,
                1L,
                List.of("comment1", "comment2", "comment3"));

        assertThat(json.parse(jsonItemDto)).usingRecursiveComparison().isEqualTo(itemDto);
    }

    @Test
    public void testItemRequestInDtoWithoutRequester() throws Exception {
        itemDto = new ItemDto(
                1L,
                "",
                "description",
                true,
                1L,
                1L,
                List.of("comment1", "comment2", "comment3"));

        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("");
    }
}
