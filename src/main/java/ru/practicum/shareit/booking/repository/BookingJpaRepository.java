package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс BookingJpaRepository является контрактом для последующей реализации,
 * создаваемой на этапе компиляции. Благодаря базовому интерфейсу JpaRepository
 * содержит ряд основных методов, в т.ч. все CRUD-операции. Добавляет ряд
 * собственных, производящим более сложные выборки.
 */
public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date);

    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 and b.bookingStatus = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findWaitingBookings(Long bookerId);


    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 and b.bookingStatus = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findRejectedBookings(Long bookerId);


    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 and b.bookingStatus <> 'REJECTED' and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastBookings(Long bookerId, LocalDateTime now);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllStuffBookingsByOwnerId(Long ownerId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentStuffBookingsByOwnerId(Long ownerId, LocalDateTime now);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and b.bookingStatus <> 'REJECTED' and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastStuffBookingsByOwnerId(Long ownerId, LocalDateTime now);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findFutureStuffBookingsByOwnerId(Long ownerId, LocalDateTime now);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where b.bookingStatus = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findWaitingStuffBookingsByOwnerId(Long ownerId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where b.bookingStatus = 'REJECTED' and u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findRejectedStuffBookingsByOwnerId(Long ownerId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and b.start < ?2 and i.id = ?3 and b.bookingStatus <> 'REJECTED' " +
            "order by b.end desc")
    List<Booking> findPreviousUserBooking(Long ownerId, LocalDateTime now, Long itemId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and b.start > ?2 and i.id = ?3 and b.bookingStatus <> 'REJECTED' " +
            "order by b.start asc")
    List<Booking> findNextUserBooking(Long ownerId, LocalDateTime now, Long itemId);

    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "join b.item as i " +
            "where i.id = ?1 and u.id = ?2 and b.end < ?3")
    List<Booking> findBookingByItemIdAndBookerId(Long itemId, Long bookerId, LocalDateTime now);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 and ((b.start < ?2 and b.end > ?2) or (b.start < ?3 and b.end > ?3))")
    Booking findBookingForDate(Long itemId, LocalDateTime start, LocalDateTime end);
}
