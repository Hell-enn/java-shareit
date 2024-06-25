package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemGetDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemGetDtoTest {
    @Autowired
    private JacksonTester<ItemGetDto> json;

    @Test
    public void testItemGetDtoSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        BookingDto lastBooking = new BookingDto(1L, now.minusDays(1), now.minusHours(5), 1L, 1L, "PAST");
        BookingDto nextBooking = new BookingDto(2L, now.plusDays(1), now.plusDays(5), 1L, 2L, "APPROVED");

        CommentDto comment1 = new CommentDto(1L, "text1", 5L, 1L, "Petr Petrov", now.minusDays(10));
        CommentDto comment2 = new CommentDto(2L, "text2", 6L, 1L, "Igor Igorev", now.minusDays(11));
        CommentDto comment3 = new CommentDto(3L, "text3", 7L, 1L, "Ilya Ilev", now.minusDays(12));

        ItemGetDto itemGetDto = new ItemGetDto(
                1L,
                "name",
                "description",
                true,
                2L,
                1L,
                lastBooking,
                nextBooking,
                List.of(comment1, comment2, comment3));

        JsonContent<ItemGetDto> result = json.write(itemGetDto);

        assertThat(json.write(itemGetDto)).hasJsonPathValue("$.description", "description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);
        }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();

        BookingDto lastBooking = new BookingDto(1L, now.minusDays(1), now.minusHours(5), 1L, 1L, "PAST");
        BookingDto nextBooking = new BookingDto(2L, now.plusDays(1), now.plusDays(5), 1L, 2L, "APPROVED");

        CommentDto comment1 = new CommentDto(1L, "text1", 5L, 1L, "Petr Petrov", now.minusDays(10));
        CommentDto comment2 = new CommentDto(2L, "text2", 6L, 1L, "Igor Igorev", now.minusDays(11));
        CommentDto comment3 = new CommentDto(3L, "text3", 7L, 1L, "Ilya Ilev", now.minusDays(12));

        ItemGetDto itemGetDto = new ItemGetDto(
                1L,
                "name",
                "description",
                true,
                1L,
                1L,
                lastBooking,
                nextBooking,
                List.of(comment1, comment2, comment3));

        String jsonItemGetDto =
                        "{\"id\":\"1\"," +
                        "\"name\":\"name\"," +
                        "\"description\":\"description\"," +
                        "\"available\":\"true\"," +
                        "\"booker\":\"1\"," +
                        "\"request\":\"1\"," +
                        "\"lastBooking\":" +
                                "{\"id\":\"1\"," +
                                "\"start\":\"" + now.minusDays(1) + "\"," +
                                "\"end\":\"" + now.minusHours(5) + "\"," +
                                "\"itemId\":\"1\"," +
                                "\"bookerId\":\"1\"," +
                                "\"status\":\"PAST\"}," +
                        "\"nextBooking\":" +
                                "{\"id\":\"2\"," +
                                "\"start\":\"" + now.plusDays(1) + "\"," +
                                "\"end\":\"" + now.plusDays(5) + "\"," +
                                "\"itemId\":\"1\"," +
                                "\"bookerId\":\"2\"," +
                                "\"status\":\"APPROVED\"}," +
                        "\"comments\":[" +
                            "{\"id\":\"1\"," +
                                "\"text\":\"text1\"," +
                                "\"author\":\"5\"," +
                                "\"item\":\"1\"," +
                                "\"authorName\":\"Petr Petrov\"," +
                                "\"created\":\"" + now.minusDays(10) + "\"}," +
                            "{\"id\":\"2\"," +
                                "\"text\":\"text2\"," +
                                "\"author\":\"6\"," +
                                "\"item\":\"1\"," +
                                "\"authorName\":\"Igor Igorev\"," +
                                "\"created\":\"" + now.minusDays(11) + "\"}," +
                            "{\"id\":\"3\"," +
                                "\"text\":\"text3\"," +
                                "\"author\":\"7\"," +
                                "\"item\":\"1\"," +
                                "\"authorName\":\"Ilya Ilev\"," +
                                "\"created\":\"" + now.minusDays(12) + "\"}" +
                        "]}";

        assertThat(json.parse(jsonItemGetDto)).usingRecursiveComparison().isEqualTo(itemGetDto);
    }

    @Test
    public void testItemRequestInDtoWithoutRequester() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        BookingDto lastBooking = new BookingDto(1L, now.minusDays(1), now.minusHours(5), 1L, 1L, "PAST");
        BookingDto nextBooking = new BookingDto(2L, now.plusDays(1), now.plusDays(5), 1L, 2L, "APPROVED");

        CommentDto comment1 = new CommentDto(1L, "text1", 5L, 1L, "Petr Petrov", now.minusDays(10));
        CommentDto comment2 = new CommentDto(2L, "text2", 6L, 1L, "Igor Igorev", now.minusDays(11));
        CommentDto comment3 = new CommentDto(3L, "text3", 7L, 1L, "Ilya Ilev", now.minusDays(12));

        ItemGetDto itemGetDto = new ItemGetDto(
                1L,
                "",
                "",
                true,
                1L,
                1L,
                lastBooking,
                nextBooking,
                List.of(comment1, comment2, comment3));

        JsonContent<ItemGetDto> result = json.write(itemGetDto);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("");
    }
}
