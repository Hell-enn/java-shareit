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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {
    @Mock
    private ItemPagingAndSortingRepository mockItemPagingAndSortingRepository;
    @Mock
    private UserJpaRepository mockUserJpaRepository;
    @Mock
    private CommentJpaRepository mockCommentJpaRepository;
    @Mock
    private BookingJpaRepository mockBookingJpaRepository;
    @Mock
    private ItemPagingAndSortingRepository mockItemJpaRepository;
    @Mock
    private CommentMapper mockCommentMapper;
    @Mock
    private ItemMapper mockItemMapper;
    private ItemService itemService;


    @BeforeEach
    private void create() {
        itemService = new ItemServiceImpl(
                mockItemPagingAndSortingRepository,
                mockUserJpaRepository,
                mockCommentJpaRepository,
                mockBookingJpaRepository,
                mockCommentMapper,
                mockItemMapper);
    }


    @Test
    public void testPostItemOk() {
        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        Item item = new Item(1L, "item1", "description2", true, user, null);
        ItemDto itemDto = new ItemDto(1L, "item1", "description2", true, 1L, null, List.of());
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemMapper.toItemDto(Mockito.any(Item.class)))
                .thenReturn(itemDto);

        Mockito
                .when(mockItemMapper.toItem(Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(item);

        Mockito
                .when(mockItemPagingAndSortingRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        ItemDto addedItemDto = itemService.postItem(1L, itemDto);
        Assertions.assertEquals(itemDto, addedItemDto);
    }


    @Test
    public void testPostItemWhenUserDoesNotExist() {
        ItemDto itemDto = new ItemDto(1L, "name1", "description2", true, 111L, null, List.of());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.postItem(1L, itemDto));

        Assertions.assertEquals("Пользователь не существует!", exception.getMessage());
    }


    @Test
    public void testPatchItemOk() {
        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        Item item = new Item(1L, "item1", "description2", true, user, null);
        ItemDto itemDto = new ItemDto(1L, "item1", "description2", true, 1L, null, List.of());
        Mockito
                .doNothing().when(mockItemMapper).updateItemFromDto(itemDto, item);

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemMapper.toItemDto(Mockito.any(Item.class)))
                .thenReturn(itemDto);

        Mockito
                .when(mockItemMapper.toItem(Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(item);

        Mockito
                .when(mockItemPagingAndSortingRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto updatedItemDto = itemService.patchItem(1L, itemDto, 1L);
        Assertions.assertEquals(itemDto, updatedItemDto);
    }


    @Test
    public void testPatchItemNull() {
        Mockito
                .when(mockItemPagingAndSortingRepository.save(Mockito.any(Item.class)))
                .thenThrow(new NotFoundException("Вещь не найдена!"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(1L, null, 1L));

        Assertions.assertEquals("Вещь не найдена!", exception.getMessage());
    }


    @Test
    public void testPatchItemWithoutItem() {
        ItemDto itemDto = new ItemDto(1L, null, "description2", true, 1L, null, List.of());

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(1L, itemDto, 1L));

        Assertions.assertEquals("Вещь не найдена!", exception.getMessage());
    }


    @Test
    public void testPatchItemUserAreNotOwner() {
        ItemDto itemDto = new ItemDto(1L, null, "description2", true, 1L, null, List.of());

        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        Item item = new Item(1L, "item1", "description2", true, user, null);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        final ForbiddenException exception = Assertions.assertThrows(
                ForbiddenException.class,
                () -> itemService.patchItem(2L, itemDto, 1L));

        Assertions.assertEquals("Пользователь не является обладателем вещи!", exception.getMessage());
    }


    @Test
    public void testPatchItemChangeOwner() {
        ItemDto itemDto = new ItemDto(1L, null, "description2", true, 2L, null, List.of());
        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        Item item = new Item(1L, "item1", "description2", true, user, null);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.patchItem(1L, itemDto, 1L));

        Assertions.assertEquals("Нельзя изменить хозяина вещи!", exception.getMessage());
    }


    @Test
    public void testPatchItemChangeRequest() {
        ItemDto itemDto = new ItemDto(1L, null, "description2", true, 1L, 2L, List.of());
        User user = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description1", requester, LocalDateTime.now());
        Item item = new Item(1L, "item1", "description2", true, user, itemRequest);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.patchItem(1L, itemDto, 1L));

        Assertions.assertEquals("Нельзя изменить информацию о запросе к вещи!", exception.getMessage());
    }


    @Test
    public void getItemsEmptyOk() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockItemPagingAndSortingRepository.findByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of());
        Mockito
                .when(mockItemMapper.toItemGetDtos(Mockito.anyList(), Mockito.anyLong()))
                .thenReturn(List.of());

        Assertions.assertEquals(List.of(), itemService.getItems(1L, 1, 10));
    }


    @Test
    public void getItemsOk() {
        User user1 = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester1 = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1L, "description1", requester1, LocalDateTime.now());
        Item item1 = new Item(1L, "item1", "description2", true, user1, itemRequest1);
        ItemGetDto itemDto1 = new ItemGetDto(
                1L, "item1", "description2", true, 1L, 1L, null, null,List.of());

        User requester2 = new User(4L, "Mikhail Mikhailov", "mikhailmikhailov@gmail.com");
        ItemRequest itemRequest2 = new ItemRequest(2L, "description3", requester2, LocalDateTime.now());
        Item item2 = new Item(2L, "item2", "description3", true, user1, itemRequest2);
        ItemGetDto itemDto2 = new ItemGetDto(
                2L, "item2", "description3", true, 1L, 2L, null, null, List.of());

        User requester3 = new User(6L, "Igor Igorev", "igorigorev@gmail.com");
        ItemRequest itemRequest3 = new ItemRequest(3L, "description5", requester3, LocalDateTime.now());
        Item item3 = new Item(3L, "item3", "description6", true, user1, itemRequest3);
        ItemGetDto itemDto3 = new ItemGetDto(
                3L, "item3", "description6", true, 1L, 3L, null, null, List.of());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockItemPagingAndSortingRepository.findByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1, item2, item3));
        Mockito
                .when(mockItemMapper.toItemGetDtos(Mockito.anyList(), Mockito.anyLong()))
                .thenReturn(List.of(itemDto1, itemDto2, itemDto3));

        Assertions.assertEquals(List.of(itemDto1, itemDto2, itemDto3), itemService.getItems(1L, 0, 10));
    }


    @Test
    public void getItemsUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItems(1L, 0, 10));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void getItemOk() {
        User user1 = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester1 = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1L, "description1", requester1, LocalDateTime.now());
        Item item1 = new Item(1L, "item1", "description2", true, user1, itemRequest1);
        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));
        ItemGetDto itemDto1 = new ItemGetDto(
                1L, "item1", "description2", true, 1L, 1L, null, null,List.of());
        Mockito
                .when(mockItemMapper.toItemGetDto(Mockito.any(Item.class), Mockito.anyLong()))
                .thenReturn(itemDto1);
        Assertions.assertEquals(new ItemGetDto(1L, "item1", "description2", true, 1L, 1L, null, null,List.of()),
                itemService.getItem(1L, 1L));
    }


    @Test
    public void getItemDoesNotExist() {
        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1L, 1L));
        Assertions.assertEquals("Вещь не найдена!", exception.getMessage());
    }


    @Test
    public void getItemsBySearchOk() {
        User user1 = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester1 = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest1 = new ItemRequest(1L, "description1", requester1, LocalDateTime.now());
        Item item1 = new Item(1L, "item1", "description2", true, user1, itemRequest1);
        ItemGetDto itemDto1 = new ItemGetDto(
                1L, "item1", "description2", true, 1L, 1L, null, null,List.of());

        User requester2 = new User(4L, "Mikhail Mikhailov", "mikhailmikhailov@gmail.com");
        ItemRequest itemRequest2 = new ItemRequest(2L, "description3", requester2, LocalDateTime.now());
        Item item2 = new Item(2L, "item2", "description3", true, user1, itemRequest2);
        ItemGetDto itemDto2 = new ItemGetDto(
                2L, "item2", "description3", true, 1L, 2L, null, null, List.of());

        User requester3 = new User(6L, "Igor Igorev", "igorigorev@gmail.com");
        ItemRequest itemRequest3 = new ItemRequest(3L, "description5", requester3, LocalDateTime.now());
        Item item3 = new Item(3L, "item3", "description6", true, user1, itemRequest3);
        ItemGetDto itemDto3 = new ItemGetDto(
                3L, "item3", "description6", true, 1L, 3L, null, null, List.of());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockItemPagingAndSortingRepository.findAllBySubstring(Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item1, item2, item3));
        Mockito
                .when(mockItemMapper.toItemGetDtos(Mockito.anyList(), Mockito.anyLong()))
                .thenReturn(List.of(itemDto1, itemDto2, itemDto3));

        Assertions.assertEquals(List.of(itemDto1, itemDto2, itemDto3), itemService.getItemsBySearch("descr", 1L, 0, 10));
    }


    @Test
    public void getItemsBySearchEmptyOk() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockItemPagingAndSortingRepository.findByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of());
        Mockito
                .when(mockItemMapper.toItemGetDtos(Mockito.anyList(), Mockito.anyLong()))
                .thenReturn(List.of());

        Assertions.assertEquals(List.of(), itemService.getItemsBySearch("", 1L, 1, 10));
    }


    @Test
    public void getItemsBySearchUserNotFound() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemsBySearch("", 1L, 0, 10));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void addCommentOk() {
        Mockito
                .when(mockItemPagingAndSortingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        User booker = new User(1L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description1", booker, LocalDateTime.now());
        Item item = new Item(1L, "item1", "description2", true, booker, itemRequest);
        Booking booking = new Booking(
                1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED);
        Mockito
                .when(mockBookingJpaRepository.findBookingByItemIdAndBookerId(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "text", 2L, 1L, "Michael222", now);
        User author = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        Comment comment = new Comment(1L, "text", author, item, now);
        Mockito
                .when(mockCommentMapper.toComment(Mockito.any(CommentDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(comment);
        Mockito
                .when(mockCommentJpaRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);
        Mockito
                .when(mockCommentMapper.toCommentDto(Mockito.any(Comment.class)))
                .thenReturn(commentDto);

        Assertions.assertEquals(commentDto, itemService.addComment(1L, commentDto, 2L));
    }


    @Test
    public void addCommentItemNotFound() {
        Mockito
                .when(mockItemPagingAndSortingRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(1L, null, 1L));

        Assertions.assertEquals("Вещь не найдена!", exception.getMessage());
    }


    @Test
    public void addCommentWithoutBooking() {
        Mockito
                .when(mockItemPagingAndSortingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findBookingByItemIdAndBookerId(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(1L, null, 1L));

        Assertions.assertEquals("Пользователь не брал вещь!", exception.getMessage());
    }


    @Test
    public void addCommentWithoutText() {
        Mockito
                .when(mockItemPagingAndSortingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        User booker = new User(1L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description1", booker, LocalDateTime.now());
        Item item = new Item(1L, "item1", "description2", true, booker, itemRequest);
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "", 2L, 1L, "Michael222", now);
        Booking booking = new Booking(
                1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED);
        Mockito
                .when(mockBookingJpaRepository.findBookingByItemIdAndBookerId(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(1L, commentDto, 1L));

        Assertions.assertEquals("В качестве отзыва передана пустая строка!", exception.getMessage());
    }
}