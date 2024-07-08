package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceTest {
    @Mock
    private RequestJpaRepository mockRequestJpaRepository;
    @Mock
    private UserJpaRepository mockUserJpaRepository;
    @Mock
    private ItemRequestMapper mockItemRequestMapper;
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void create() {
        itemRequestService = new ItemRequestServiceImpl(
                mockRequestJpaRepository,
                mockUserJpaRepository,
                mockItemRequestMapper);
    }


    @Test
    public void testPostItemRequestOk() {
        LocalDateTime now = LocalDateTime.now();
        User requester = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, now);
        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto(1L, "description", 1L, now, List.of());
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(1L, "description", 1L);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockItemRequestMapper.toItemRequest(Mockito.any(ItemRequestInDto.class), Mockito.anyLong()))
                .thenReturn(itemRequest);
        Mockito
                .when(mockRequestJpaRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        Mockito
                .when(mockItemRequestMapper.toItemRequestOutDto(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestOutDto);
        Assertions.assertEquals(itemRequestService.postItemRequest(1L, itemRequestInDto), itemRequestOutDto);
    }


    @Test
    public void testPostItemRequestWithoutExistingUser() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.postItemRequest(1L, null));

        Assertions.assertEquals("Пользователь не существует!", exception.getMessage());
    }


    @Test
    public void testPatchItemRequestOk() {
        LocalDateTime now = LocalDateTime.now();
        User requester = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, now);
        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto(1L, "description", 1L, now, List.of());
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(1L, "description", 1L);

        Mockito
                .when(mockRequestJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(mockItemRequestMapper.toItemRequest(Mockito.any(ItemRequestInDto.class), Mockito.anyLong()))
                .thenReturn(itemRequest);
        Mockito
                .when(mockRequestJpaRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        Mockito
                .when(mockItemRequestMapper.toItemRequestOutDto(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestOutDto);
        Assertions.assertEquals(itemRequestService.patchItemRequest(1L, itemRequestInDto, 1L), itemRequestOutDto);
    }


    @Test
    public void testPatchItemRequestWhenUserIsNotRequester() {
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(1L, "description", 1L);
        LocalDateTime now = LocalDateTime.now();
        User requester = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, now);

        Mockito
                .when(mockRequestJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemRequestService.patchItemRequest(2L, itemRequestInDto, 1L));
        Assertions.assertEquals("Пользователь не может редактировать запрос!", exception.getMessage());
    }


    @Test
    public void testPatchItemRequestNotFound() {
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(1L, "description", 1L);

        Mockito
                .when(mockRequestJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.patchItemRequest(2L, itemRequestInDto, 1L));
        Assertions.assertEquals("Запрос не найден!", exception.getMessage());
    }


    @Test
    public void testGetItemRequestsOk() {
        LocalDateTime now = LocalDateTime.now();
        User requester = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1L, "description", requester, now);
        ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(1L, "description", 1L, now, List.of());

        ItemRequest itemRequest2 = new ItemRequest(2L, "description2", requester, now);
        ItemRequestOutDto itemRequestOutDto2 = new ItemRequestOutDto(2L, "description2", 1L, now, List.of());

        ItemRequest itemRequest3 = new ItemRequest(3L, "description3", requester, now);
        ItemRequestOutDto itemRequestOutDto3 = new ItemRequestOutDto(3L, "description3", 1L, now, List.of());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockRequestJpaRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest1, itemRequest2, itemRequest3));
        Mockito
                .when(mockItemRequestMapper.toItemRequestOutDtos(Mockito.anyList()))
                .thenReturn(List.of(itemRequestOutDto1, itemRequestOutDto2, itemRequestOutDto3));

        Assertions.assertEquals(
                itemRequestService.getItemRequests(1L), List.of(itemRequestOutDto1, itemRequestOutDto2, itemRequestOutDto3));
    }


    @Test
    public void testGetItemRequestsWhenUserDoesNotExist() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequests(2L));
        Assertions.assertEquals("Пользователь не существует!", exception.getMessage());
    }


    @Test
    public void testGetItemRequestOk() {
        LocalDateTime now = LocalDateTime.now();
        User requester = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1L, "description", requester, now);
        ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(1L, "description", 1L, now, List.of());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockRequestJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest1));
        Mockito
                .when(mockItemRequestMapper.toItemRequestOutDto(itemRequest1))
                .thenReturn(itemRequestOutDto1);

        Assertions.assertEquals(
                itemRequestService.getItemRequest(1L, 1L),
                new ItemRequestOutDto(1L, "description", 1L, now, List.of()));
    }


    @Test
    public void testGetItemRequestWhenUserDoesNotExist() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, 1L));
        Assertions.assertEquals("Пользователь не существует!", exception.getMessage());
    }


    @Test
    public void testGetItemRequestNotFound() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockRequestJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(1L, 1L));
        Assertions.assertEquals("Запрос не найден!", exception.getMessage());
    }


    @Test
    public void getAllItemRequestsOk() {
        LocalDateTime now = LocalDateTime.now();
        User requester = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1L, "description", requester, now);
        ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(1L, "description", 1L, now, List.of());

        ItemRequest itemRequest2 = new ItemRequest(2L, "description2", requester, now);
        ItemRequestOutDto itemRequestOutDto2 = new ItemRequestOutDto(2L, "description2", 1L, now, List.of());

        ItemRequest itemRequest3 = new ItemRequest(3L, "description3", requester, now);
        ItemRequestOutDto itemRequestOutDto3 = new ItemRequestOutDto(3L, "description3", 1L, now, List.of());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockRequestJpaRepository.findAllInPage(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemRequest1, itemRequest2, itemRequest3));
        Mockito
                .when(mockItemRequestMapper.toItemRequestOutDtos(Mockito.anyList()))
                .thenReturn(List.of(itemRequestOutDto1, itemRequestOutDto2, itemRequestOutDto3));

        Assertions.assertEquals(
                itemRequestService.getAllItemRequests(1L, 1, 10), List.of(itemRequestOutDto1, itemRequestOutDto2, itemRequestOutDto3));
    }


    @Test
    public void getAllItemRequestsWhenUserDoesNotExist() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getAllItemRequests(1L, 1, 10));
        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }
}