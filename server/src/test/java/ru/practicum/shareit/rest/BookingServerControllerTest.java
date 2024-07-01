package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.controller.BookingServerController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingServerController.class)
public class BookingServerControllerTest {
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;


    @Test
    public void testPostBookingOk() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 2L, 2L, List.of());
        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");

        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);
        BookingOutcomingDto bookingOutcomingDto = new BookingOutcomingDto(1L, start, end, itemDto, booker, "WAITING");
        when(bookingService.postBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenReturn(bookingOutcomingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutcomingDto.getId()), Long.class))
                //.andExpect(jsonPath("$.start", is(bookingOutcomingDto.getStart().toString().substring(0, bookingOutcomingDto.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$.end", is(bookingOutcomingDto.getEnd().toString().substring(0, bookingOutcomingDto.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$.item", is(bookingOutcomingDto.getItem())))
                //.andExpect(jsonPath("$.booker", is(bookingOutcomingDto.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingOutcomingDto.getStatus())));
    }


    @Test
    public void testPostBookingNotFoundException() throws Exception {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);

        when(bookingService.postBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testPostBookingBadRequestException() throws Exception {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);

        when(bookingService.postBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPostBookingValidationException() throws Exception {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);

        when(bookingService.postBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testApproveBookingOk() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 2L, 2L, List.of());
        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");

        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);
        BookingOutcomingDto bookingOutcomingDto = new BookingOutcomingDto(1L, start, end, itemDto, booker, "WAITING");
        when(bookingService.patchBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(bookingOutcomingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutcomingDto.getId()), Long.class))
                //.andExpect(jsonPath("$.start", is(bookingOutcomingDto.getStart().toString().substring(0, bookingOutcomingDto.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$.end", is(bookingOutcomingDto.getEnd().toString().substring(0, bookingOutcomingDto.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$.item", is(bookingOutcomingDto.getItem())))
                //.andExpect(jsonPath("$.booker", is(bookingOutcomingDto.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingOutcomingDto.getStatus())));
    }


    @Test
    public void testApproveBookingNotFoundException() throws Exception {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);

        when(bookingService.patchBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetBookingOk() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 2L, 2L, List.of());
        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");

        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);
        BookingOutcomingDto bookingOutcomingDto = new BookingOutcomingDto(1L, start, end, itemDto, booker, "WAITING");
        when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingOutcomingDto);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutcomingDto.getId()), Long.class))
                //.andExpect(jsonPath("$.start", is(bookingOutcomingDto.getStart().toString().substring(0, bookingOutcomingDto.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$.end", is(bookingOutcomingDto.getEnd().toString().substring(0, bookingOutcomingDto.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$.item", is(bookingOutcomingDto.getItem())))
                //.andExpect(jsonPath("$.booker", is(bookingOutcomingDto.getBooker())))
                .andExpect(jsonPath("$.status", is(bookingOutcomingDto.getStatus())));
    }


    @Test
    public void testGetBookingNotFoundException() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, null);

        when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetUserBookingsOk() throws Exception {

        LocalDateTime start1 = LocalDateTime.now().plusDays(1);
        LocalDateTime end1 = LocalDateTime.now().plusDays(2);
        LocalDateTime start2 = LocalDateTime.now().plusDays(2);
        LocalDateTime end2 = LocalDateTime.now().plusDays(3);
        LocalDateTime start3 = LocalDateTime.now().plusDays(3);
        LocalDateTime end3 = LocalDateTime.now().plusDays(4);

        BookingDto bookingDto = new BookingDto(1L, start1, end1, 1L, 1L, null);
        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");

        ItemDto itemDto1 = new ItemDto(1L, "name1", "description1", true, 2L, 2L, List.of());
        ItemDto itemDto2 = new ItemDto(2L, "name2", "description2", true, 3L, 4L, List.of());
        ItemDto itemDto3 = new ItemDto(3L, "name3", "description3", true, 4L, 6L, List.of());

        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(1L, start1, end1, itemDto1, booker, "WAITING");
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(2L, start2, end2, itemDto2, booker, "WAITING");
        BookingOutcomingDto bookingOutcomingDto3 = new BookingOutcomingDto(3L, start3, end3, itemDto3, booker, "WAITING");

        when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2, bookingOutcomingDto3));

        mvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutcomingDto1.getId()), Long.class))
                //.andExpect(jsonPath("$[0].start", is(bookingOutcomingDto1.getStart().toString().substring(0, bookingOutcomingDto1.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$[0].end", is(bookingOutcomingDto1.getEnd().toString().substring(0, bookingOutcomingDto1.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$[0].item", is(bookingOutcomingDto1.getItem())))
                //.andExpect(jsonPath("$[0].booker", is(bookingOutcomingDto1.getBooker())))
                .andExpect(jsonPath("$[1].status", is(bookingOutcomingDto1.getStatus())))
                .andExpect(jsonPath("$[1].id", is(bookingOutcomingDto2.getId()), Long.class))
                //.andExpect(jsonPath("$[1].start", is(bookingOutcomingDto2.getStart().toString().substring(0, bookingOutcomingDto2.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$[1].end", is(bookingOutcomingDto2.getEnd().toString().substring(0, bookingOutcomingDto2.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$[1].item", is(bookingOutcomingDto2.getItem())))
                //.andExpect(jsonPath("$[1].booker", is(bookingOutcomingDto2.getBooker())))
                .andExpect(jsonPath("$[1].status", is(bookingOutcomingDto2.getStatus())))
                .andExpect(jsonPath("$[2].id", is(bookingOutcomingDto3.getId()), Long.class))
                //.andExpect(jsonPath("$[2].start", is(bookingOutcomingDto3.getStart().toString().substring(0, bookingOutcomingDto3.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$[2].end", is(bookingOutcomingDto3.getEnd().toString().substring(0, bookingOutcomingDto3.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$[2].item", is(bookingOutcomingDto3.getItem())))
                //.andExpect(jsonPath("$[2].booker", is(bookingOutcomingDto3.getBooker())))
                .andExpect(jsonPath("$[2].status", is(bookingOutcomingDto3.getStatus())));
    }


    @Test
    public void testGetUserBookingsNotFoundException() throws Exception {

        when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetUserBookingsBadRequestException() throws Exception {

        when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testGetUserBookingsRuntimeException() throws Exception {

        when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(RuntimeException.class);

        mvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    public void testGetUserBookingsUnsupportedOperationException() throws Exception {

        when(bookingService.getBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(UnsupportedOperationException.class);

        mvc.perform(get("/bookings?state=UNSUPPORTED&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testGetUserStuffBookingsOk() throws Exception {

        LocalDateTime start1 = LocalDateTime.now().plusDays(1);
        LocalDateTime end1 = LocalDateTime.now().plusDays(2);
        LocalDateTime start2 = LocalDateTime.now().plusDays(2);
        LocalDateTime end2 = LocalDateTime.now().plusDays(3);
        LocalDateTime start3 = LocalDateTime.now().plusDays(3);
        LocalDateTime end3 = LocalDateTime.now().plusDays(4);

        BookingDto bookingDto = new BookingDto(1L, start1, end1, 1L, 1L, null);
        UserDto booker = new UserDto(1L, "Petr Petrov", "petrpetrov@gmail.com");

        ItemDto itemDto1 = new ItemDto(1L, "name1", "description1", true, 2L, 2L, List.of());
        ItemDto itemDto2 = new ItemDto(2L, "name2", "description2", true, 2L, 4L, List.of());
        ItemDto itemDto3 = new ItemDto(3L, "name3", "description3", true, 2L, 6L, List.of());

        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(1L, start1, end1, itemDto1, booker, "WAITING");
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(2L, start2, end2, itemDto2, booker, "WAITING");
        BookingOutcomingDto bookingOutcomingDto3 = new BookingOutcomingDto(3L, start3, end3, itemDto3, booker, "WAITING");

        when(bookingService.getUserStuffBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2, bookingOutcomingDto3));

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutcomingDto1.getId()), Long.class))
                //.andExpect(jsonPath("$[0].start", is(bookingOutcomingDto1.getStart().toString().substring(0, bookingOutcomingDto1.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$[0].end", is(bookingOutcomingDto1.getEnd().toString().substring(0, bookingOutcomingDto1.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$[0].item", is(bookingOutcomingDto1.getItem())))
                //.andExpect(jsonPath("$[0].booker", is(bookingOutcomingDto1.getBooker())))
                .andExpect(jsonPath("$[1].status", is(bookingOutcomingDto1.getStatus())))
                .andExpect(jsonPath("$[1].id", is(bookingOutcomingDto2.getId()), Long.class))
                //.andExpect(jsonPath("$[1].start", is(bookingOutcomingDto2.getStart().toString().substring(0, bookingOutcomingDto2.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$[1].end", is(bookingOutcomingDto2.getEnd().toString().substring(0, bookingOutcomingDto2.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$[1].item", is(bookingOutcomingDto2.getItem())))
                //.andExpect(jsonPath("$[1].booker", is(bookingOutcomingDto2.getBooker())))
                .andExpect(jsonPath("$[1].status", is(bookingOutcomingDto2.getStatus())))
                .andExpect(jsonPath("$[2].id", is(bookingOutcomingDto3.getId()), Long.class))
                //.andExpect(jsonPath("$[2].start", is(bookingOutcomingDto3.getStart().toString().substring(0, bookingOutcomingDto3.getStart().toString().length() - 2))))
                //.andExpect(jsonPath("$[2].end", is(bookingOutcomingDto3.getEnd().toString().substring(0, bookingOutcomingDto3.getEnd().toString().length() - 2))))
                //.andExpect(jsonPath("$[2].item", is(bookingOutcomingDto3.getItem())))
                //.andExpect(jsonPath("$[2].booker", is(bookingOutcomingDto3.getBooker())))
                .andExpect(jsonPath("$[2].status", is(bookingOutcomingDto3.getStatus())));
    }


    @Test
    public void testGetUserStuffBookingsNotFoundException() throws Exception {

        when(bookingService.getUserStuffBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetUserStuffBookingsBadRequestException() throws Exception {

        when(bookingService.getUserStuffBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testGetUserStuffBookingsRuntimeException() throws Exception {

        when(bookingService.getUserStuffBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(RuntimeException.class);

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    public void testGetUserStuffBookingsUnsupportedOperationException() throws Exception {

        when(bookingService.getUserStuffBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(UnsupportedOperationException.class);

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .content(mapper.writeValueAsString(null))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }
}