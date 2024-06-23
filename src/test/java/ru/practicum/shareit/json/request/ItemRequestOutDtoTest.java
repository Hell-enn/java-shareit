package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestOutDtoTest {
    @Autowired
    private JacksonTester<ItemRequestOutDto> json;
    @Valid
    private ItemRequestOutDto itemRequestOutDto;

    @Test
    public void testItemRequestOutDtoSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        itemRequestOutDto = new ItemRequestOutDto(
                1L,
                "description",
                1L,
                now,
                List.of());

        JsonContent<ItemRequestOutDto> result = json.write(itemRequestOutDto);

        assertThat(json.write(itemRequestOutDto)).hasJsonPathValue("$.description", "description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
        //assertThat(result).extractingJsonPathValue("$.created").isEqualTo(now.toString().substring(0, now.toString().length() - 2));
        assertThat(result).extractingJsonPathValue("$.items").isEqualTo(List.of());
    }


    @Test
    public void testItemRequestOutDtoDeserialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.toString();
        String jsonItemRequestInDto = "{\"id\":\"1\",\"description\":\"description\",\"requestor\":\"1\",\"created\":\"" + nowStr + "\",\"items\":[]}";
        itemRequestOutDto = new ItemRequestOutDto(
                1L,
                "description",
                1L,
                now,
                List.of());

        assertThat(json.parse(jsonItemRequestInDto)).usingRecursiveComparison().isEqualTo(itemRequestOutDto);
    }
}
