package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.dto.ItemGetTestDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private MockMvc mvc;
    private ItemPostDto itemDto = new ItemPostDto(
            1L, "name", "description", true, 1L, 1L, List.of());
    private ItemGetTestDto itemGetDto = new ItemGetTestDto(
            1L, "name", "description", true, 1L, 1L, null, null, List.of());


    @Test
    public void testPostItemOk() throws Exception {
        when(itemClient.postItem(Mockito.anyLong(), Mockito.any(ItemPostDto.class)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }


    @Test
    public void testPostItemNotFoundException() throws Exception {
        when(itemClient.postItem(Mockito.anyLong(), any(ItemPostDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testPostItemValidationException() throws Exception {
        when(itemClient.postItem(Mockito.anyLong(), any(ItemPostDto.class)))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPostItemBadRequestException() throws Exception {
        when(itemClient.postItem(Mockito.anyLong(), any(ItemPostDto.class)))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPatchItemOk() throws Exception {
        ItemPatchDto itemDto1 = new ItemPatchDto("name1", "description1", true, 1L, 1L, List.of());

        when(itemClient.patchItem(Mockito.anyLong(), Mockito.any(ItemPatchDto.class), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(itemDto1, HttpStatus.OK));

        mvc.perform(patch("/items/" + 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto1.getOwner()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));
    }


    @Test
    public void testPatchItemBadRequestException() throws Exception {
        when(itemClient.patchItem(Mockito.anyLong(), any(ItemPatchDto.class), Mockito.anyLong()))
                .thenThrow(BadRequestException.class);

        mvc.perform(patch("/items/" + 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPatchItemNotFoundException() throws Exception {
        when(itemClient.patchItem(Mockito.anyLong(), any(ItemPatchDto.class), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/items/" + 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testPatchItemForbiddenException() throws Exception {
        when(itemClient.patchItem(Mockito.anyLong(), any(ItemPatchDto.class), Mockito.anyLong()))
                .thenThrow(ForbiddenException.class);

        mvc.perform(patch("/items/" + 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(403));
    }


    @Test
    public void testGetItemsOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startBooking1 = now.minusDays(10);
        LocalDateTime endBooking1 = now.minusDays(5);
        LocalDateTime startBooking2 = now.plusDays(1);
        LocalDateTime endBooking2 = now.plusDays(3);

        BookingDto lastBookingDto1 = new BookingDto(1L, startBooking1, endBooking1, 2L, 1L, "APPROVED");
        BookingDto nextBookingDto1 = new BookingDto(2L, startBooking2, endBooking2, 2L, 1L, "APPROVED");

        LocalDateTime startBooking3 = now.minusDays(8);
        LocalDateTime endBooking3 = now.minusDays(7);
        LocalDateTime startBooking4 = now.plusDays(2);
        LocalDateTime endBooking4 = now.plusDays(4);

        BookingDto lastBookingDto2 = new BookingDto(3L, startBooking3, endBooking3, 3L, 3L, "APPROVED");
        BookingDto nextBookingDto2 = new BookingDto(4L, startBooking4, endBooking4, 3L, 3L, "APPROVED");

        ItemGetTestDto itemGetDto1 = new ItemGetTestDto(
                2L, "name2", "description2", true, 1L, 1L, lastBookingDto1, nextBookingDto1, List.of());
        ItemGetTestDto itemGetDto2 = new ItemGetTestDto(
                3L, "name3", "description3", true, 3L, 2L, lastBookingDto2, nextBookingDto2, List.of());

        List<ItemGetTestDto> items = List.of(itemGetDto, itemGetDto1, itemGetDto2);
        when(itemClient.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));

        ResultActions ra = mvc.perform(get("/items")
                .content(mapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        mvc.perform(get("/items?from=0&size=20")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemGetDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemGetDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemGetDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemGetDto.getAvailable())))
                .andExpect(jsonPath("$[0].booker", is(itemGetDto.getBooker()), Long.class))
                .andExpect(jsonPath("$[0].request", is(itemGetDto.getRequest()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemGetDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemGetDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemGetDto.getComments())))
                .andExpect(jsonPath("$[1].id", is(itemGetDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemGetDto1.getName())))
                .andExpect(jsonPath("$[1].description", is(itemGetDto1.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemGetDto1.getAvailable())))
                .andExpect(jsonPath("$[1].booker", is(itemGetDto1.getBooker()), Long.class))
                .andExpect(jsonPath("$[1].request", is(itemGetDto1.getRequest()), Long.class))
                //.andExpect(jsonPath("$[1].lastBooking", is(itemGetDto1.getLastBooking())))
                //.andExpect(jsonPath("$[1].nextBooking", is(itemGetDto1.getNextBooking())))
                .andExpect(jsonPath("$[1].comments", is(itemGetDto2.getComments())))
                .andExpect(jsonPath("$[2].id", is(itemGetDto2.getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(itemGetDto2.getName())))
                .andExpect(jsonPath("$[2].description", is(itemGetDto2.getDescription())))
                .andExpect(jsonPath("$[2].available", is(itemGetDto2.getAvailable())))
                .andExpect(jsonPath("$[2].booker", is(itemGetDto2.getBooker()), Long.class))
                .andExpect(jsonPath("$[2].request", is(itemGetDto2.getRequest()), Long.class))
                //.andExpect(jsonPath("$[2].lastBooking", is(itemGetDto2.getLastBooking())))
                //.andExpect(jsonPath("$[2].nextBooking", is(itemGetDto2.getNextBooking())))
                .andExpect(jsonPath("$[2].comments", is(itemGetDto2.getComments())));
    }


    @Test
    public void testGetItemsNotFoundException() throws Exception {

        when(itemClient.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items?from=0&size=20")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetItemsBadRequestException() throws Exception {

        when(itemClient.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/items?from=0&size=20")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testGetItemsRuntimeException() throws Exception {

        when(itemClient.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(RuntimeException.class);

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    public void testGetItemOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startBooking1 = now.minusDays(10);
        LocalDateTime endBooking1 = now.minusDays(5);
        LocalDateTime startBooking2 = now.plusDays(1);
        LocalDateTime endBooking2 = now.plusDays(3);

        BookingDto lastBookingDto1 = new BookingDto(1L, startBooking1, endBooking1, 2L, 1L, "APPROVED");
        BookingDto nextBookingDto1 = new BookingDto(2L, startBooking2, endBooking2, 2L, 1L, "APPROVED");

        ItemGetTestDto itemGetDto1 = new ItemGetTestDto(
                2L, "name2", "description2", true, 1L, 1L, lastBookingDto1, nextBookingDto1, List.of());

        when(itemClient.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(itemGetDto1, HttpStatus.OK));

        mvc.perform(get("/items/" + 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemGetDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemGetDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemGetDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemGetDto1.getAvailable())))
                .andExpect(jsonPath("$.booker", is(itemGetDto1.getBooker()), Long.class))
                .andExpect(jsonPath("$.request", is(itemGetDto1.getRequest()), Long.class))
                //.andExpect(jsonPath("$.lastBooking", is(itemGetDto1.getLastBooking())))
                //.andExpect(jsonPath("$.nextBooking", is(itemGetDto1.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemGetDto1.getComments())));
    }


    @Test
    public void testGetItemNotFoundException() throws Exception {

        when(itemClient.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items/" + 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetItemsBySearchOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startBooking1 = now.minusDays(10);
        LocalDateTime endBooking1 = now.minusDays(5);
        LocalDateTime startBooking2 = now.plusDays(1);
        LocalDateTime endBooking2 = now.plusDays(3);

        BookingDto lastBookingDto1 = new BookingDto(1L, startBooking1, endBooking1, 2L, 1L, "APPROVED");
        BookingDto nextBookingDto1 = new BookingDto(2L, startBooking2, endBooking2, 2L, 1L, "APPROVED");

        LocalDateTime startBooking3 = now.minusDays(8);
        LocalDateTime endBooking3 = now.minusDays(7);
        LocalDateTime startBooking4 = now.plusDays(2);
        LocalDateTime endBooking4 = now.plusDays(4);

        BookingDto lastBookingDto2 = new BookingDto(3L, startBooking3, endBooking3, 3L, 3L, "APPROVED");
        BookingDto nextBookingDto2 = new BookingDto(4L, startBooking4, endBooking4, 3L, 3L, "APPROVED");

        ItemGetTestDto itemGetDto1 = new ItemGetTestDto(
                2L, "name2", "description2", true, 1L, 1L, lastBookingDto1, nextBookingDto1, List.of());
        ItemGetTestDto itemGetDto2 = new ItemGetTestDto(
                3L, "name3", "description3", true, 3L, 2L, lastBookingDto2, nextBookingDto2, List.of());

        List<ItemGetTestDto> items = List.of(itemGetDto, itemGetDto1, itemGetDto2);
        when(itemClient.getItemsBySearch(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));

        mvc.perform(get("/items/search?text=descr&from=0&size=20")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemGetDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemGetDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemGetDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemGetDto.getAvailable())))
                .andExpect(jsonPath("$[0].booker", is(itemGetDto.getBooker()), Long.class))
                .andExpect(jsonPath("$[0].request", is(itemGetDto.getRequest()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemGetDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemGetDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemGetDto.getComments())))
                .andExpect(jsonPath("$[1].id", is(itemGetDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemGetDto1.getName())))
                .andExpect(jsonPath("$[1].description", is(itemGetDto1.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemGetDto1.getAvailable())))
                .andExpect(jsonPath("$[1].booker", is(itemGetDto1.getBooker()), Long.class))
                .andExpect(jsonPath("$[1].request", is(itemGetDto1.getRequest()), Long.class))
                //.andExpect(jsonPath("$[1].lastBooking", is(itemGetDto1.getLastBooking())))
                //.andExpect(jsonPath("$[1].nextBooking", is(itemGetDto1.getNextBooking())))
                .andExpect(jsonPath("$[1].comments", is(itemGetDto2.getComments())))
                .andExpect(jsonPath("$[2].id", is(itemGetDto2.getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(itemGetDto2.getName())))
                .andExpect(jsonPath("$[2].description", is(itemGetDto2.getDescription())))
                .andExpect(jsonPath("$[2].available", is(itemGetDto2.getAvailable())))
                .andExpect(jsonPath("$[2].booker", is(itemGetDto2.getBooker()), Long.class))
                .andExpect(jsonPath("$[2].request", is(itemGetDto2.getRequest()), Long.class))
                //.andExpect(jsonPath("$[2].lastBooking", is(itemGetDto2.getLastBooking())))
                //.andExpect(jsonPath("$[2].nextBooking", is(itemGetDto2.getNextBooking())))
                .andExpect(jsonPath("$[2].comments", is(itemGetDto2.getComments())));
    }


    @Test
    public void testGetItemsBySearchNotFoundException() throws Exception {

        when(itemClient.getItemsBySearch(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items/search?text=descr&from=0&size=20")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetItemsBySearchBadRequestException() throws Exception {

        when(itemClient.getItemsBySearch(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/items/search?text=descr&from=0&size=20")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testGetItemsBySearchRuntimeException() throws Exception {

        when(itemClient.getItemsBySearch(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(RuntimeException.class);

        mvc.perform(get("/items/search?text=descr")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    public void testAddCommentOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "text", 1L, 1L, "Petr Petrov", now);
        when(itemClient.addComment(Mockito.anyLong(), any(CommentDto.class), Mockito.anyLong()))
                .thenReturn(new ResponseEntity<>(commentDto, HttpStatus.OK));

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.author", is(commentDto.getAuthor()), Long.class))
                .andExpect(jsonPath("$.item", is(commentDto.getItem()), Long.class))
                //.andExpect(jsonPath("$.created", is(commentDto.getCreated())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }


    @Test
    public void testAddCommentNotFoundException() throws Exception {

        when(itemClient.addComment(Mockito.anyLong(), any(CommentDto.class), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testAddCommentBadRequestException() throws Exception {

        when(itemClient.addComment(Mockito.anyLong(), any(CommentDto.class), Mockito.anyLong()))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }
}