package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    @Valid
    private BookingDto bookingDto;

    @Test
    public void testBookingDtoSerialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        bookingDto = new BookingDto(
                1L,
                now.minusDays(1),
                now.plusHours(2),
                1L,
                1L,
                "APPROVED");

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(json.write(bookingDto)).hasJsonPathValue("$.status", "status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        //assertThat(result).extractingJsonPathValue("$.start").isEqualTo(now.minusDays(1).toString().substring(0, now.minusDays(1).toString().length() - 2));
        //assertThat(result).extractingJsonPathValue("$.end").isEqualTo(now.plusHours(2).toString().substring(0, now.plusHours(2).toString().length() - 2));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }


    @Test
    public void testItemRequestInDtoDeserialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        bookingDto = new BookingDto(
                1L,
                now.minusDays(1),
                now.plusHours(2),
                1L,
                1L,
                "APPROVED");

        String jsonBookingDto =
                        "{\"id\":\"1\"," +
                        "\"start\":\"" + now.minusDays(1) + "\"," +
                        "\"end\":\"" + now.plusHours(2) + "\"," +
                        "\"itemId\":\"1\"," +
                        "\"bookerId\":\"1\"," +
                        "\"status\":\"APPROVED\"}";

        assertThat(json.parse(jsonBookingDto)).usingRecursiveComparison().isEqualTo(bookingDto);
    }
}
