package ru.practicum.shareit.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.junit.jupiter.api.Test;

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


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    private ItemRequestInDto itemRequestInDto = new ItemRequestInDto(
            1L, "description", 1L);
    private ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto(
            1L, "description", 1L, LocalDateTime.now(), List.of());


    @Test
    public void testPostItemRequestOk() throws Exception {
        when(itemRequestService.postItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class)))
                .thenReturn(itemRequestOutDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(Math.toIntExact(itemRequestOutDto.getRequestor()))));
    }


    @Test
    public void testPostItemRequestNotFoundException() throws Exception {
        when(itemRequestService.postItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testPostItemRequestValidationException() throws Exception {
        when(itemRequestService.postItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class)))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPostItemRequestBadRequestException() throws Exception {
        when(itemRequestService.postItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class)))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPatchItemRequestOk() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        ItemRequestInDto itemRequestInDto1 = new ItemRequestInDto(
                1L, "new description", 2L);
        ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(
                1L, "new description", 1L, now, List.of());

        when(itemRequestService.patchItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestOutDto1);

        mvc.perform(patch("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestInDto1))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutDto1.getDescription())))
                .andExpect(jsonPath("$.requestor", is(Math.toIntExact(itemRequestOutDto1.getRequestor()))));
    }


    @Test
    public void testPatchItemRequestValidationException() throws Exception {

        when(itemRequestService.patchItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class), Mockito.anyLong()))
                .thenThrow(ValidationException.class);

        mvc.perform(patch("/requests/" + 1)
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testPatchItemRequestNotFoundException() throws Exception {

        when(itemRequestService.patchItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/requests/" + 1)
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testPatchItemRequestBadRequestException() throws Exception {

        when(itemRequestService.patchItemRequest(Mockito.anyLong(), any(ItemRequestInDto.class), Mockito.anyLong()))
                .thenThrow(BadRequestException.class);

        mvc.perform(patch("/requests/" + 1)
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }


    @Test
    public void testGetItemRequestsOk() throws Exception {
        ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(
                2L, "description1", 1L, LocalDateTime.now(), List.of());
        ItemRequestOutDto itemRequestOutDto2 = new ItemRequestOutDto(
                3L, "description2", 1L, LocalDateTime.now(), List.of());

        when(itemRequestService.getItemRequests(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestOutDto, itemRequestOutDto1, itemRequestOutDto2));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(itemRequestOutDto.getDescription())))
                .andExpect(jsonPath("$[1].description", is(itemRequestOutDto1.getDescription())))
                .andExpect(jsonPath("$[2].description", is(itemRequestOutDto2.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestOutDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$[1].requestor", is(itemRequestOutDto1.getRequestor()), Long.class))
                .andExpect(jsonPath("$[2].requestor", is(itemRequestOutDto2.getRequestor()), Long.class));
    }


    @Test
    public void testGetItemRequestsNotFoundException() throws Exception {

        when(itemRequestService.getItemRequests(Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetItemRequestOk() throws Exception {
        when(itemRequestService.getItemRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestOutDto);

        mvc.perform(get("/requests/" + 1)
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestOutDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestOutDto.getRequestor()), Long.class));
    }


    @Test
    public void testGetItemRequestNotFoundException() throws Exception {

        when(itemRequestService.getItemRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/" + 1)
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetAllItemRequestsOk() throws Exception {
        ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(
                2L, "description1", 2L, LocalDateTime.now(), List.of());
        ItemRequestOutDto itemRequestOutDto2 = new ItemRequestOutDto(
                3L, "description2", 3L, LocalDateTime.now(), List.of());

        when(itemRequestService.getAllItemRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestOutDto, itemRequestOutDto1, itemRequestOutDto2));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(itemRequestOutDto.getDescription())))
                .andExpect(jsonPath("$[1].description", is(itemRequestOutDto1.getDescription())))
                .andExpect(jsonPath("$[2].description", is(itemRequestOutDto2.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestOutDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$[1].requestor", is(itemRequestOutDto1.getRequestor()), Long.class))
                .andExpect(jsonPath("$[2].requestor", is(itemRequestOutDto2.getRequestor()), Long.class));
    }


    @Test
    public void testGetAllItemRequestsNotFoundException() throws Exception {

        when(itemRequestService.getAllItemRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }


    @Test
    public void testGetAllItemRequestsBadRequestException() throws Exception {

        when(itemRequestService.getAllItemRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }
}
