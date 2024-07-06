package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Модель данных бронирования, используемая на уровне репозитория.
 */
@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", insertable = false, updatable = false)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(start, booking.start) && Objects.equals(end, booking.end) && Objects.equals(item, booking.item) && Objects.equals(booker, booking.booker) && bookingStatus == booking.bookingStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, item, booker, bookingStatus);
    }
}
