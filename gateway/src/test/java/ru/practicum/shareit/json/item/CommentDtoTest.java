package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    public void testCommentDtoSerialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(
                1L,
                "text",
                1L,
                1L,
                "Petr Petrov",
                now);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(json.write(commentDto)).hasJsonPathValue("$.text", "text");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathNumberValue("$.author").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Petr Petrov");
        //assertThat(result).extractingJsonPathValue("$.created").isEqualTo(now.toString().substring(0, now.toString().length() - 2));
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(
                1L,
                "text",
                1L,
                1L,
                "Petr Petrov",
                now);

        String jsonCommentDto =
                "{\"id\":\"1\"," +
                        "\"text\":\"text\"," +
                        "\"author\":\"1\"," +
                        "\"item\":\"1\"," +
                        "\"authorName\":\"Petr Petrov\"," +
                        "\"created\":\"" + now + "\"}";

        assertThat(json.parse(jsonCommentDto)).usingRecursiveComparison().isEqualTo(commentDto);
    }

    @Test
    public void testItemRequestInDtoWithoutRequester() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(
                1L,
                "",
                null,
                null,
                "Petr Petrov",
                now);

        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("");
        assertThat(result).extractingJsonPathStringValue("$.author").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item").isEqualTo(null);
    }
}