package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findFirstByItemAndBookerIdAndStatusAndEndBookingBeforeOrderByStartBooking(
            Item item, Integer bookerId, BookingEnum status, LocalDateTime now);

    List<Booking> findAllByBookerIdOrderByStartBookingDesc(int userId);

    List<Booking> findAllByItemAndStatus(Item items, BookingEnum status);

    List<Booking> findAllByItemInOrderByStartBookingDesc(List<Item> items);

    List<Booking> findAllByItemIn(List<Item> items);

    List<Booking> findAllByItem(Item item);

}
