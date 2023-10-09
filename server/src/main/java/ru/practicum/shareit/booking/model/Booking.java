package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking implements Comparable<Booking> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false)
    private Integer id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startBooking;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endBooking;
    @ManyToOne(targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    private Item item;
    @Column(name = "booker_id", nullable = false)
    private Integer bookerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingEnum status;

    @Override
    public int compareTo(Booking o) {
        return this.getStartBooking().compareTo(o.getStartBooking());
    }
}
