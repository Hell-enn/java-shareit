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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedOperationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    @Mock
    private BookingJpaRepository mockBookingJpaRepository;
    @Mock
    private UserJpaRepository mockUserJpaRepository;
    @Mock
    private ItemPagingAndSortingRepository mockItemPagingAndSortingRepository;
    @Mock
    private BookingMapper mockBookingMapper;
    private BookingService bookingService;

    @BeforeEach
    public void create() {
        bookingService = new BookingServiceImpl(
                mockBookingJpaRepository,
                mockUserJpaRepository,
                mockItemPagingAndSortingRepository,
                mockBookingMapper
        );
    }


    @Test
    public void testPostBookingOk() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, null);
        BookingOutcomingDto bookingOutcomingDto = new BookingOutcomingDto(1L, start, end, itemDto, bookerDto, BookingStatus.WAITING.getDescription());
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockBookingMapper.toBooking(Mockito.any(BookingDto.class), Mockito.anyLong()))
                .thenReturn(booking);
        Mockito
                .when(mockBookingJpaRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);
        Mockito
                .when(mockBookingMapper.toBookingOutcomingDto(Mockito.any(Booking.class)))
                .thenReturn(bookingOutcomingDto);

        Assertions.assertEquals(bookingOutcomingDto, bookingService.postBooking(3L, bookingDto));
    }


    @Test
    public void testPostBookingWhenUserDoesNotExist() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.postBooking(1L, bookingDto));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenItemAbsent() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.postBooking(1L, bookingDto));

        Assertions.assertEquals("Вещь не найдена!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenItemNotAvailable() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", false, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.postBooking(1L, bookingDto));

        Assertions.assertEquals("Вещь недоступна!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenThereIsAnotherBooking() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "AlexeyAlexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());
        Booking crossedBooking = new Booking(2L, start.plusHours(1), end.minusHours(1), item, booker, BookingStatus.APPROVED);

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(crossedBooking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.postBooking(1L, bookingDto));

        Assertions.assertEquals("Найдено другое бронирование на эти даты!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenOwnerIsBooker() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 1L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.postBooking(1L, bookingDto));

        Assertions.assertEquals("Хозяин вещи не может её забронировать!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenEndBeforeStart() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.postBooking(3L, bookingDto));

        Assertions.assertEquals("Дата начала бронирования не может превышать дату окончания!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenStartBeforeCurrentMoment() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingDto bookingDto = new BookingDto(1L, start, end, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.postBooking(3L, bookingDto));

        Assertions.assertEquals("Дата начала бронирования меньше текущего момента времени!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenStartIsEqualToEnd() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime moment = LocalDateTime.now().plusHours(1);
        BookingDto bookingDto = new BookingDto(1L, moment, moment, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.postBooking(3L, bookingDto));

        Assertions.assertEquals("Время окончания не может быть равно времени начала!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenStartIsAbsent() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime moment = LocalDateTime.now().plusHours(1);
        BookingDto bookingDto = new BookingDto(1L, null, moment, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.postBooking(3L, bookingDto));

        Assertions.assertEquals("Время начала или окончания бронирования не задано!", exception.getMessage());
    }


    @Test
    public void testPostBookingWhenEndIsAbsent() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime moment = LocalDateTime.now().plusHours(1);
        BookingDto bookingDto = new BookingDto(1L, moment, null, 1L, 3L, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mockItemPagingAndSortingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mockBookingJpaRepository.findBookingForDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.postBooking(3L, bookingDto));

        Assertions.assertEquals("Время начала или окончания бронирования не задано!", exception.getMessage());
    }


    @Test
    public void testPatchBookingOk() {

        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingOutcomingDto bookingOutcomingDto = new BookingOutcomingDto(1L, start, end, itemDto, bookerDto, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(mockBookingMapper.toBookingOutcomingDto(Mockito.any(Booking.class)))
                .thenReturn(bookingOutcomingDto);

        Assertions.assertEquals(bookingOutcomingDto, bookingService.patchBooking(1L, true, 1L));
    }


    @Test
    public void testPatchBookingWithoutBooking() {
        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.patchBooking(1L, true, 1L));

        Assertions.assertEquals("Бронирование не найдено!", exception.getMessage());
    }


    @Test
    public void testPatchBookingWithoutUser() {
        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.patchBooking(1L, true, 1L));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void testPatchBookingAlreadyApproved() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);

        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.patchBooking(1L, true, 1L));

        Assertions.assertEquals("Бронирование уже подтверждено!", exception.getMessage());
    }


    @Test
    public void testPatchBookingWithoutItem() {
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, null, booker, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.patchBooking(1L, true, 1L));

        Assertions.assertEquals("Отсутствует ссылка на вещь!", exception.getMessage());
    }


    @Test
    public void testPatchBookingWithoutOwner() {
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, null, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.patchBooking(1L, true, 1L));

        Assertions.assertEquals("Отсутствует ссылка на хозяина вещи!", exception.getMessage());
    }


    @Test
    public void testPatchBookingOwnerIsRequester() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.patchBooking(3L, true, 1L));

        Assertions.assertEquals("Данный пользователь не может изменять статус бронирования!", exception.getMessage());
    }


    @Test
    public void testGetBookingOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
        BookingOutcomingDto bookingOutcomingDto = new BookingOutcomingDto(1L, start, end, itemDto, bookerDto, BookingStatus.WAITING.getDescription());

        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(mockBookingMapper.toBookingOutcomingDto(Mockito.any(Booking.class)))
                .thenReturn(bookingOutcomingDto);

        Assertions.assertEquals(bookingOutcomingDto, bookingService.getBooking(1L, 1L));
    }


    @Test
    public void testGetBookingWithoutBooking() {
        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));

        Assertions.assertEquals("Бронирование не найдено!", exception.getMessage());
    }


    @Test
    public void testGetBookingWithoutItem() {
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, null, booker, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));

        Assertions.assertEquals("Отсутствует ссылка на вещь!", exception.getMessage());
    }


    @Test
    public void testGetBookingWithoutOwner() {
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, null, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));

        Assertions.assertEquals("Отсутствует ссылка на хозяина вещи!", exception.getMessage());
    }


    @Test
    public void testGetBookingWithoutBooker() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, null, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));

        Assertions.assertEquals("Отсутствует ссылка на автора бронирования!", exception.getMessage());
    }


    @Test
    public void testGetBookingWhenUserNeitherBookerNorOwner() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);

        Mockito
                .when(mockBookingJpaRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 11L));

        Assertions.assertEquals("Данный пользователь не может получить информацию о бронировании!", exception.getMessage());
    }


    @Test
    public void testGetBookingsAllOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findByBookerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getBookings(1L, "ALL", 1, 10));
    }


    @Test
    public void testGetBookingsCurrentOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getBookings(1L, "CURRENT", 1, 10));
    }


    @Test
    public void testGetBookingsPastOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findPastBookings(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getBookings(1L, "PAST", 1, 10));
    }


    @Test
    public void testGetBookingsFutureOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getBookings(1L, "FUTURE", 1, 10));
    }


    @Test
    public void testGetBookingsWaitingOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findWaitingBookings(
                        Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getBookings(1L, "WAITING", 1, 10));
    }


    @Test
    public void testGetBookingsRejectedOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findRejectedBookings(
                        Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getBookings(1L, "REJECTED", 1, 10));
    }


    @Test
    public void testGetBookingsUnsupportedOperation() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        final UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> bookingService.getBookings(1L, "UNSUPPORTED", 1, 10));

        Assertions.assertEquals("{\"error\":\"Unknown state: UNSUPPORTED\"}", exception.getMessage());
    }


    @Test
    public void testGetBookingsUserNotFound() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookings(1L, "UNSUPPORTED", 1, 10));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }


    @Test
    public void testGetUserStuffBookingsALLOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findAllStuffBookingsByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getUserStuffBookings(1L, "ALL", 1, 10));
    }


    @Test
    public void testGetUserStuffBookingsCurrentOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findCurrentStuffBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getUserStuffBookings(1L, "CURRENT", 1, 10));
    }


    @Test
    public void testGetUserStuffBookingsPastOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findPastStuffBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getUserStuffBookings(1L, "PAST", 1, 10));
    }


    @Test
    public void testGetUserStuffBookingsFutureOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findFutureStuffBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getUserStuffBookings(1L, "FUTURE", 1, 10));
    }


    @Test
    public void testGetUserStuffBookingsWaitingOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findWaitingStuffBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getUserStuffBookings(1L, "WAITING", 1, 10));
    }


    @Test
    public void testGetUserStuffBookingsRejectedOk() {
        User owner = new User(1L, "Ivan Ivanov", "ivanivanov@gmail.com");
        User requester = new User(2L, "Petr Petrov", "petrpetrov@gmail.com");
        User booker = new User(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto = new UserDto(3L, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);
        ItemDto itemDto = new ItemDto(
                1L, "name", "description", true, 1L, 1L, List.of());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking1 = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto1 = new BookingOutcomingDto(
                1L, start, end, itemDto, bookerDto, BookingStatus.APPROVED.getDescription());
        Booking booking2 = new Booking(2L, start.plusDays(3), end.plusDays(3), item, booker, BookingStatus.APPROVED);
        BookingOutcomingDto bookingOutcomingDto2 = new BookingOutcomingDto(
                2L, start.plusDays(3), end.plusDays(3), itemDto, bookerDto, BookingStatus.APPROVED.getDescription());

        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(mockBookingJpaRepository.findRejectedStuffBookingsByOwnerId(
                        Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking1, booking2));
        Mockito
                .when(mockBookingMapper.bookingOutcomingDtoList(Mockito.anyList()))
                .thenReturn(List.of(bookingOutcomingDto1, bookingOutcomingDto2));

        Assertions.assertEquals(
                List.of(bookingOutcomingDto1, bookingOutcomingDto2), bookingService.getUserStuffBookings(1L, "REJECTED", 1, 10));
    }


    @Test
    public void testGetUserStuffBookingsUnsupportedOperation() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        final UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> bookingService.getUserStuffBookings(1L, "UNSUPPORTED", 1, 10));

        Assertions.assertEquals("{\"error\":\"Unknown state: UNSUPPORTED\"}", exception.getMessage());
    }


    @Test
    public void testGetUserStuffBookingsUserNotFound() {
        Mockito
                .when(mockUserJpaRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserStuffBookings(1L, "UNSUPPORTED", 1, 10));

        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }
}