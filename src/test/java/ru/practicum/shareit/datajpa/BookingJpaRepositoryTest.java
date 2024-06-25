package ru.practicum.shareit.datajpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingJpaRepositoryTest {
    @Autowired
    private BookingJpaRepository bookingJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemPagingAndSortingRepository itemPagingAndSortingRepository;

    @Test
    public void testFindWaitingBookings() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner, null);
        Item item2 = new Item(null, "name2", "description2", true, owner, null);
        Item item3 = new Item(null, "name3", "description3", true, owner, null);
        Item item4 = new Item(null, "name4", "description4", true, owner, null);
        Item item5 = new Item(null, "name5", "description5", true, owner, null);
        Item item6 = new Item(null, "name6", "description6", true, owner, null);

        Booking booking1 = new Booking(null, now.plusHours(1), now.plusHours(2), item1, booker1, BookingStatus.WAITING);
        Booking booking2 = new Booking(null, now.plusHours(3), now.plusHours(4), item2, booker1, BookingStatus.WAITING);
        Booking booking3 = new Booking(null, now.plusHours(5), now.plusHours(6), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.plusHours(7), now.plusHours(8), item4, booker3, BookingStatus.WAITING);
        Booking booking5 = new Booking(null, now.plusHours(9), now.plusHours(10), item5, booker1, BookingStatus.WAITING);
        Booking booking6 = new Booking(null, now.plusHours(11), now.plusHours(12), item6, booker1, BookingStatus.WAITING);

        userJpaRepository.save(owner);
        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking6, booking5, booking2, booking1);
        List<Booking> foundBookings = bookingJpaRepository.findWaitingBookings(booker1.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking4);
        foundBookings = bookingJpaRepository.findWaitingBookings(booker3.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findWaitingBookings(booker4.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindRejectedBookings() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner, null);
        Item item2 = new Item(null, "name2", "description2", true, owner, null);
        Item item3 = new Item(null, "name3", "description3", true, owner, null);
        Item item4 = new Item(null, "name4", "description4", true, owner, null);
        Item item5 = new Item(null, "name5", "description5", true, owner, null);
        Item item6 = new Item(null, "name6", "description6", true, owner, null);

        Booking booking1 = new Booking(null, now.plusHours(1), now.plusHours(2), item1, booker1, BookingStatus.REJECTED);
        Booking booking2 = new Booking(null, now.plusHours(3), now.plusHours(4), item2, booker1, BookingStatus.REJECTED);
        Booking booking3 = new Booking(null, now.plusHours(5), now.plusHours(6), item3, booker1, BookingStatus.WAITING);
        Booking booking4 = new Booking(null, now.plusHours(7), now.plusHours(8), item4, booker3, BookingStatus.REJECTED);
        Booking booking5 = new Booking(null, now.plusHours(9), now.plusHours(10), item5, booker1, BookingStatus.REJECTED);
        Booking booking6 = new Booking(null, now.plusHours(11), now.plusHours(12), item6, booker1, BookingStatus.REJECTED);

        userJpaRepository.save(owner);
        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking6, booking5, booking2, booking1);
        List<Booking> foundBookings = bookingJpaRepository.findRejectedBookings(booker1.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking4);
        foundBookings = bookingJpaRepository.findRejectedBookings(booker3.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findRejectedBookings(booker4.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindPastBookings() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner, null);
        Item item2 = new Item(null, "name2", "description2", true, owner, null);
        Item item3 = new Item(null, "name3", "description3", true, owner, null);
        Item item4 = new Item(null, "name4", "description4", true, owner, null);
        Item item5 = new Item(null, "name5", "description5", true, owner, null);
        Item item6 = new Item(null, "name6", "description6", true, owner, null);

        Booking booking1 = new Booking(null, now.minusHours(2), now.minusHours(1), item1, booker1, BookingStatus.PAST);
        Booking booking2 = new Booking(null, now.minusHours(4), now.minusHours(3), item2, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.minusHours(10), now.minusHours(9), item5, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(11), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.minusHours(14), now.minusHours(13), item6, booker3, BookingStatus.APPROVED);

        userJpaRepository.save(owner);
        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking1, booking2, booking5, booking6);
        List<Booking> foundBookings = bookingJpaRepository.findPastBookings(booker1.getId(), LocalDateTime.now(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking7);
        foundBookings = bookingJpaRepository.findPastBookings(booker3.getId(), LocalDateTime.now(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findPastBookings(booker4.getId(), LocalDateTime.now(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindAllStuffBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner2, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(2), now.minusHours(1), item1, booker1, BookingStatus.PAST);
        Booking booking2 = new Booking(null, now.minusHours(4), now.minusHours(3), item2, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.minusHours(10), now.minusHours(9), item5, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(11), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.minusHours(14), now.minusHours(13), item6, booker3, BookingStatus.APPROVED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking1, booking2, booking5);
        List<Booking> foundBookings = bookingJpaRepository.findAllStuffBookingsByOwnerId(owner1.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking6, booking7);
        foundBookings = bookingJpaRepository.findAllStuffBookingsByOwnerId(owner3.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findAllStuffBookingsByOwnerId(booker1.getId(), page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindCurrentStuffBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner2, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(2), now.plusHours(1), item1, booker1, BookingStatus.PAST);
        Booking booking2 = new Booking(null, now.minusHours(4), now.plusHours(3), item2, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.minusHours(10), now.plusHours(9), item5, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.plusHours(11), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.minusHours(14), now.plusHours(13), item6, booker3, BookingStatus.APPROVED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking1, booking2, booking5);
        List<Booking> foundBookings = bookingJpaRepository.findCurrentStuffBookingsByOwnerId(owner1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking6, booking7);
        foundBookings = bookingJpaRepository.findCurrentStuffBookingsByOwnerId(owner3.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findCurrentStuffBookingsByOwnerId(booker1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindPastStuffBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(2), now.minusHours(1), item1, booker1, BookingStatus.PAST);
        Booking booking2 = new Booking(null, now.minusHours(4), now.minusHours(3), item2, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.minusHours(10), now.minusHours(9), item5, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(11), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.minusHours(14), now.minusHours(13), item6, booker3, BookingStatus.APPROVED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking1, booking2, booking5);
        List<Booking> foundBookings = bookingJpaRepository.findPastStuffBookingsByOwnerId(owner1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking6, booking7);
        foundBookings = bookingJpaRepository.findPastStuffBookingsByOwnerId(owner3.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findPastStuffBookingsByOwnerId(booker1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindFutureStuffBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.plusHours(2), now.plusHours(10), item1, booker1, BookingStatus.PAST);
        Booking booking2 = new Booking(null, now.plusHours(4), now.plusHours(30), item2, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.minusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.plusHours(10), now.plusHours(90), item5, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.plusHours(12), now.plusHours(110), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker3, BookingStatus.APPROVED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking5, booking2, booking1);
        List<Booking> foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(owner1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking7, booking6);
        foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(owner3.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(booker1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindWaitingStuffBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.plusHours(2), now.plusHours(10), item1, booker1, BookingStatus.WAITING);
        Booking booking2 = new Booking(null, now.plusHours(4), now.plusHours(30), item2, booker1, BookingStatus.WAITING);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.REJECTED);
        Booking booking4 = new Booking(null, now.minusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.plusHours(10), now.plusHours(90), item5, booker1, BookingStatus.WAITING);
        Booking booking6 = new Booking(null, now.plusHours(12), now.plusHours(110), item6, booker1, BookingStatus.WAITING);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker3, BookingStatus.WAITING);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking5, booking2, booking1);
        List<Booking> foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(owner1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking7, booking6);
        foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(owner3.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(booker1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindRejectedStuffBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.plusHours(2), now.plusHours(10), item1, booker1, BookingStatus.REJECTED);
        Booking booking2 = new Booking(null, now.plusHours(4), now.plusHours(30), item2, booker1, BookingStatus.REJECTED);
        Booking booking3 = new Booking(null, now.minusHours(6), now.minusHours(5), item3, booker1, BookingStatus.WAITING);
        Booking booking4 = new Booking(null, now.minusHours(8), now.plusHours(7), item4, booker1, BookingStatus.PAST);
        Booking booking5 = new Booking(null, now.plusHours(10), now.plusHours(90), item5, booker1, BookingStatus.REJECTED);
        Booking booking6 = new Booking(null, now.plusHours(12), now.plusHours(110), item6, booker1, BookingStatus.REJECTED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker3, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Pageable page = PageRequest.of(0, 5);

        List<Booking> waitingBookings = List.of(booking5, booking2, booking1);
        List<Booking> foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(owner1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking7, booking6);
        foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(owner3.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findFutureStuffBookingsByOwnerId(booker1.getId(), now, page);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindPreviousUserBooking() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(2), now.plusHours(10), item1, booker1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusHours(4), now.plusHours(30), item1, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.plusHours(6), now.plusHours(50), item3, booker1, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(70), item4, booker1, BookingStatus.APPROVED);
        Booking booking5 = new Booking(null, now.minusHours(10), now.minusHours(7), item1, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.plusHours(110), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.minusHours(14), now.plusHours(130), item6, booker3, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Booking previousBooking = booking2;
        Booking foundBooking = bookingJpaRepository.findPreviousUserBooking(owner1.getId(), now, item1.getId());
        Assertions.assertEquals(previousBooking, foundBooking);

        previousBooking = booking6;
        foundBooking = bookingJpaRepository.findPreviousUserBooking(owner3.getId(), now, item6.getId());
        Assertions.assertEquals(previousBooking, foundBooking);

        previousBooking = null;
        foundBooking = bookingJpaRepository.findPreviousUserBooking(booker1.getId(), now, item2.getId());
        Assertions.assertEquals(previousBooking, foundBooking);
    }


    @Test
    public void testFindNextUserBooking() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.plusHours(2), now.plusHours(4), item1, booker1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.plusHours(5), now.plusHours(30), item1, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.plusHours(6), now.plusHours(50), item3, booker1, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(70), item4, booker1, BookingStatus.APPROVED);
        Booking booking5 = new Booking(null, now.plusHours(40), now.plusHours(70), item1, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.plusHours(12), now.plusHours(13), item6, booker1, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker3, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Booking nextBooking = booking1;
        Booking foundBooking = bookingJpaRepository.findNextUserBooking(owner1.getId(), now, item1.getId());
        Assertions.assertEquals(nextBooking, foundBooking);

        nextBooking = booking6;
        foundBooking = bookingJpaRepository.findNextUserBooking(owner3.getId(), now, item6.getId());
        Assertions.assertEquals(nextBooking, foundBooking);

        nextBooking = null;
        foundBooking = bookingJpaRepository.findNextUserBooking(booker1.getId(), now, item2.getId());
        Assertions.assertEquals(nextBooking, foundBooking);
    }


    @Test
    public void testFindBookingByItemIdAndBookerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(6), now.minusHours(4), item1, booker1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusHours(3), now.minusHours(2), item1, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.plusHours(6), now.plusHours(50), item3, booker1, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(70), item1, booker1, BookingStatus.APPROVED);
        Booking booking5 = new Booking(null, now.minusHours(1), now.minusMinutes(5), item1, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(10), item6, booker2, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker2, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        List<Booking> waitingBookings = List.of(booking1, booking2, booking5);
        List<Booking> foundBookings = bookingJpaRepository.findBookingByItemIdAndBookerId(item1.getId(), booker1.getId(), now);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of(booking6);
        foundBookings = bookingJpaRepository.findBookingByItemIdAndBookerId(item6.getId(), booker2.getId(), now);
        Assertions.assertEquals(waitingBookings, foundBookings);

        waitingBookings = List.of();
        foundBookings = bookingJpaRepository.findBookingByItemIdAndBookerId(item5.getId(), booker4.getId(), now);
        Assertions.assertEquals(waitingBookings, foundBookings);
    }


    @Test
    public void testFindBookingForDate() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(6), now.minusHours(4), item1, booker1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusHours(3), now.minusHours(2), item1, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.plusHours(6), now.plusHours(50), item3, booker1, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(70), item1, booker1, BookingStatus.APPROVED);
        Booking booking5 = new Booking(null, now.minusHours(1), now.minusMinutes(5), item1, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(10), item6, booker2, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker2, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        Booking nextBooking = booking1;
        Booking foundBooking = bookingJpaRepository.findBookingForDate(item1.getId(), now.minusHours(5), now.minusHours(3));
        Assertions.assertEquals(nextBooking, foundBooking);

        nextBooking = booking6;
        foundBooking = bookingJpaRepository.findBookingForDate(item6.getId(), now.minusHours(11), now.minusHours(7));
        Assertions.assertEquals(nextBooking, foundBooking);

        nextBooking = null;
        foundBooking = bookingJpaRepository.findBookingForDate(item3.getId(), now.minusHours(50), now.minusHours(6));
        Assertions.assertEquals(nextBooking, foundBooking);
    }


    @Test
    public void testFindAmountByBookerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(6), now.minusHours(4), item1, booker1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusHours(3), now.minusHours(2), item1, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.plusHours(6), now.plusHours(50), item3, booker1, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(70), item1, booker1, BookingStatus.APPROVED);
        Booking booking5 = new Booking(null, now.minusHours(1), now.minusMinutes(5), item1, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(10), item6, booker2, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker2, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        int bookingAmount = 5;
        int foundBookingAmount = bookingJpaRepository.findAmountByBookerId(booker1.getId());
        Assertions.assertEquals(bookingAmount, foundBookingAmount);

        bookingAmount = 2;
        foundBookingAmount = bookingJpaRepository.findAmountByBookerId(booker2.getId());
        Assertions.assertEquals(bookingAmount, foundBookingAmount);

        bookingAmount = 0;
        foundBookingAmount = bookingJpaRepository.findAmountByBookerId(booker3.getId());
        Assertions.assertEquals(bookingAmount, foundBookingAmount);
    }


    @Test
    public void testFindStuffBookingsAmountByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User owner2 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");
        User owner3 = new User(null, "Igor Igorev", "igorigorev@gmail.com");

        User booker1 = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User booker2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User booker3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User booker4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        Item item1 = new Item(null, "name1", "description1", true, owner1, null);
        Item item2 = new Item(null, "name2", "description2", true, owner1, null);
        Item item3 = new Item(null, "name3", "description3", true, owner1, null);
        Item item4 = new Item(null, "name4", "description4", true, owner2, null);
        Item item5 = new Item(null, "name5", "description5", true, owner1, null);
        Item item6 = new Item(null, "name6", "description6", true, owner3, null);

        Booking booking1 = new Booking(null, now.minusHours(6), now.minusHours(4), item1, booker1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, now.minusHours(3), now.minusHours(2), item1, booker1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null, now.plusHours(6), now.plusHours(50), item3, booker1, BookingStatus.APPROVED);
        Booking booking4 = new Booking(null, now.plusHours(8), now.plusHours(70), item1, booker1, BookingStatus.APPROVED);
        Booking booking5 = new Booking(null, now.minusHours(1), now.minusMinutes(5), item1, booker1, BookingStatus.APPROVED);
        Booking booking6 = new Booking(null, now.minusHours(12), now.minusHours(10), item6, booker2, BookingStatus.APPROVED);
        Booking booking7 = new Booking(null, now.plusHours(14), now.plusHours(130), item6, booker2, BookingStatus.REJECTED);

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(owner3);

        userJpaRepository.save(booker1);
        userJpaRepository.save(booker2);
        userJpaRepository.save(booker3);
        userJpaRepository.save(booker4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);
        bookingJpaRepository.save(booking4);
        bookingJpaRepository.save(booking5);
        bookingJpaRepository.save(booking6);
        bookingJpaRepository.save(booking7);

        int bookingAmount = 5;
        int foundBookingAmount = bookingJpaRepository.findStuffBookingsAmountByOwnerId(owner1.getId());
        Assertions.assertEquals(bookingAmount, foundBookingAmount);

        bookingAmount = 0;
        foundBookingAmount = bookingJpaRepository.findStuffBookingsAmountByOwnerId(owner2.getId());
        Assertions.assertEquals(bookingAmount, foundBookingAmount);

        bookingAmount = 2;
        foundBookingAmount = bookingJpaRepository.findStuffBookingsAmountByOwnerId(owner3.getId());
        Assertions.assertEquals(bookingAmount, foundBookingAmount);
    }
}
