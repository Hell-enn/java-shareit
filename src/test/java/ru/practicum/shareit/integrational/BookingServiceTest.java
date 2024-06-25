package ru.practicum.shareit.integrational;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutcomingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @Test
    public void testGetUserStuffBookingsOk() {

        UserDto bookerDto1 = new UserDto(null, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserDto bookerDto2 = new UserDto(null, "Petr Petrov", "petrpetrov@gmail.com");
        UserDto bookerDto3 = new UserDto(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto bookerDto4 = new UserDto(null, "Andrey Andreev", "andreyandreev@gmail.com");

        UserDto postedBooker4 = userService.postUser(bookerDto4);

        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto1 = new ItemDto(1L, "name1", "description1", true, postedBooker4.getId(), null, List.of());
        ItemDto itemDto2 = new ItemDto(2L, "name2", "description2", true, postedBooker4.getId(), null, List.of());
        ItemDto itemDto3 = new ItemDto(3L, "name3", "description3", true, postedBooker4.getId(), null, List.of());

        UserDto postedBooker1 = userService.postUser(bookerDto1);
        UserDto postedBooker2 = userService.postUser(bookerDto2);
        UserDto postedBooker3 = userService.postUser(bookerDto3);

        ItemDto postedItem1 = itemService.postItem(postedBooker4.getId(), itemDto1);
        ItemDto postedItem2 = itemService.postItem(postedBooker4.getId(), itemDto2);
        ItemDto postedItem3 = itemService.postItem(postedBooker4.getId(), itemDto3);

        BookingDto bookingDto1 = new BookingDto(1L, now.plusDays(10), now.plusDays(11), postedItem1.getId(), postedBooker1.getId(), "WAITING");
        BookingDto bookingDto2 = new BookingDto(2L, now.plusDays(6), now.plusDays(7), postedItem2.getId(), postedBooker2.getId(), "WAITING");
        BookingDto bookingDto3 = new BookingDto(3L, now.plusDays(4), now.plusDays(5), postedItem3.getId(), postedBooker3.getId(), "WAITING");

        BookingOutcomingDto addedBookingDto1 = bookingService.postBooking(postedBooker1.getId(), bookingDto1);
        BookingOutcomingDto addedBookingDto2 = bookingService.postBooking(postedBooker2.getId(), bookingDto2);
        BookingOutcomingDto addedBookingDto3 = bookingService.postBooking(postedBooker3.getId(), bookingDto3);

        TypedQuery<Booking> query = em.createQuery("select b from Booking as b " +
                                                      "join b.item as i " +
                                                      "join i.owner as u " +
                                                      "where u.id = :userId " +
                                                      "order by b.start desc", Booking.class);

        List<BookingOutcomingDto> bookingOutcomingDtoList = bookingMapper.bookingOutcomingDtoList(query.setParameter("userId", postedBooker4.getId()).getResultList());

        assertThat(bookingOutcomingDtoList, notNullValue());
        assertThat(bookingOutcomingDtoList.size(), equalTo(List.of(addedBookingDto1, addedBookingDto2, addedBookingDto3).size()));
        assertThat(bookingOutcomingDtoList, equalTo(List.of(addedBookingDto1, addedBookingDto2, addedBookingDto3)));
    }
}
