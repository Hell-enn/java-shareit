package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingOutcomingDtoTest {
    @Autowired
    private JacksonTester<BookingOutcomingDto> json;
    @Valid
    private BookingOutcomingDto bookingOutcomingDto;

    @Test
    public void testBookingDtoSerialization() throws Exception {
        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemDto item = new ItemDto(1L, "name", "description", true, 2L, 2L, List.of("comment1", "comment2", "comment3"));

        LocalDateTime now = LocalDateTime.now();
        bookingOutcomingDto = new BookingOutcomingDto(
                1L,
                now.minusDays(1),
                now.plusHours(2),
                item,
                booker,
                "APPROVED");

        JsonContent<BookingOutcomingDto> result = json.write(bookingOutcomingDto);

        assertThat(json.write(bookingOutcomingDto)).hasJsonPathValue("$.status", "status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        //assertThat(result).extractingJsonPathValue("$.start").isEqualTo(now.minusDays(1).toString().substring(0, now.minusDays(1).toString().length() - 2));
        //assertThat(result).extractingJsonPathValue("$.end").isEqualTo(now.plusHours(2).toString().substring(0, now.plusHours(2).toString().length() - 2));
        //assertThat(result).extractingJsonPathValue("$.item").isEqualTo(item);
        //assertThat(result).extractingJsonPathValue("$.booker").isEqualTo(booker);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {

        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemDto item = new ItemDto(1L, "name", "description", true, 2L, 2L, List.of("comment1", "comment2", "comment3"));

        LocalDateTime now = LocalDateTime.now();
        bookingOutcomingDto = new BookingOutcomingDto(
                1L,
                now.minusDays(1),
                now.plusHours(2),
                item,
                booker,
                "APPROVED");

        String jsonBookingOutcomingDto =
                "{\"id\":\"1\"," +
                        "\"start\":\"" + now.minusDays(1) + "\"," +
                        "\"end\":\"" + now.plusHours(2) + "\"," +
                        "\"item\":{" +
                            "\"id\":\"1\"," +
                            "\"name\":\"name\"," +
                            "\"description\":\"description\"," +
                            "\"available\":\"true\"," +
                            "\"owner\":\"2\"," +
                            "\"requestId\":\"2\"," +
                            "\"comments\":[\"comment1\",\"comment2\",\"comment3\"]" +
                        "}," +
                        "\"booker\":{" +
                            "\"id\":\"1\"," +
                            "\"name\":\"Petr Petrov\"," +
                            "\"email\":\"petrpetrov@gmail.com\"" +
                        "}," +
                        "\"status\":\"APPROVED\"}";

        assertThat(json.parse(jsonBookingOutcomingDto)).usingRecursiveComparison().isEqualTo(bookingOutcomingDto);
    }

    @Test
    public void testItemRequestInDtoWithoutRequester() throws Exception {

        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemDto item = new ItemDto(1L, "name", "description", true, 2L, 2L, List.of("comment1", "comment2", "comment3"));

        LocalDateTime now = LocalDateTime.now();
        bookingOutcomingDto = new BookingOutcomingDto(
                1L,
                now.minusDays(1),
                now.plusHours(2),
                null,
                null,
                "APPROVED");

        JsonContent<BookingOutcomingDto> result = json.write(bookingOutcomingDto);
        assertThat(result).extractingJsonPathStringValue("$.item").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.booker").isEqualTo(null);
    }
}
