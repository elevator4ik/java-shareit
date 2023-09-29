package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startBooking;
    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endBooking;
    @ManyToOne(targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    private Item item;
    @NotNull
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
