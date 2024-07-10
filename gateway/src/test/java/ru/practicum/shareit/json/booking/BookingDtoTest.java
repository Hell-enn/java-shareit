package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;

@JsonTest
@Validated
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    public void testBookingDtoSerialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto(
                1L,
                now.minusDays(1),
                now.plusHours(2),
                1L,
                1L,
                "APPROVED");

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(json.write(bookingDto)).hasJsonPathValue("$.status", "status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }


    @Test
    public void testBookingDtoDeserialization() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto(
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