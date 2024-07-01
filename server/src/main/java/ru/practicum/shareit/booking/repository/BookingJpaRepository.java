package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс BookingJpaRepository является контрактом для последующей реализации,
 * создаваемой на этапе компиляции. Благодаря базовому интерфейсу JpaRepository
 * содержит ряд основных методов, в т.ч. все CRUD-операции. Добавляет ряд
 * собственных, производящим более сложные выборки.
 */
public interface BookingJpaRepository extends PagingAndSortingRepository<Booking, Long>, CrudRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable page);

    List<Booking> findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2, Pageable page);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date, Pageable page);

    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 and b.bookingStatus = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findWaitingBookings(Long bookerId, Pageable page);

    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 and b.bookingStatus = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findRejectedBookings(Long bookerId, Pageable page);

    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 and b.bookingStatus <> 'REJECTED' and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastBookings(Long bookerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllStuffBookingsByOwnerId(Long ownerId, Pageable page);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentStuffBookingsByOwnerId(Long ownerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and b.bookingStatus <> 'REJECTED' and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastStuffBookingsByOwnerId(Long ownerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where u.id = ?1 and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findFutureStuffBookingsByOwnerId(Long ownerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where b.bookingStatus = 'WAITING' " +
            "order by b.start desc")
    List<Booking> findWaitingStuffBookingsByOwnerId(Long ownerId, Pageable page);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as u " +
            "where b.bookingStatus = 'REJECTED' and u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findRejectedStuffBookingsByOwnerId(Long ownerId, Pageable page);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on b.item_id = i.item_id " +
            "join users as u on i.user_id = u.user_id " +
            "where u.user_id = ?1 and b.start_date < ?2 and i.item_id = ?3 and b.status <> 'REJECTED' " +
            "order by b.end_date desc " +
            "limit 1 ", nativeQuery = true)
    Booking findPreviousUserBooking(Long ownerId, LocalDateTime now, Long itemId);

    @Query(value = "select b.* from bookings as b " +
            "join items as i on b.item_id = i.item_id " +
            "join users as u on i.user_id = u.user_id " +
            "where u.user_id = ?1 and b.start_date > ?2 and i.item_id = ?3 and b.status <> 'REJECTED' " +
            "order by b.start_date asc " +
            "limit 1", nativeQuery = true)
    Booking findNextUserBooking(Long ownerId, LocalDateTime now, Long itemId);

    @Query("select b from Booking as b " +
            "join b.booker as u " +
            "join b.item as i " +
            "where i.id = ?1 and u.id = ?2 and b.end < ?3")
    List<Booking> findBookingByItemIdAndBookerId(Long itemId, Long bookerId, LocalDateTime now);

    @Query(value = "SELECT b.* FROM bookings AS b " +
            "JOIN items AS i ON b.item_id = i.item_id " +
            "WHERE i.item_id = ?1 AND ((b.start_date <= ?2 AND b.end_date >= ?2) or (b.start_date <= ?3 AND b.end_date >= ?3))" +
            "ORDER BY start_date ASC " +
            "LIMIT 1", nativeQuery = true)
    Booking findBookingForDate(Long itemId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT count(b.*) FROM bookings AS b WHERE b.user_id = ?1", nativeQuery = true)
    int findAmountByBookerId(Long bookerId);

    @Query(value = "select count(*) from bookings as b " +
            "join items as i on b.item_id = i.item_id " +
            "join users as u on i.user_id = u.user_id " +
            "where u.user_id = ?1", nativeQuery = true)
    int findStuffBookingsAmountByOwnerId(Long ownerId);
}
