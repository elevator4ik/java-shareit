package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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

    List<Booking> findAllByBookerId(int userId, Sort sort);

    List<Booking> findAllByBookerIdAndStatusEquals(int userId, BookingEnum state, Sort sort);

    List<Booking> findAllByBookerIdAndEndBookingIsBefore(int userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartBookingIsAfter(int userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartBookingIsBeforeAndEndBookingIsAfter(int userId,
                                                                               LocalDateTime now,
                                                                               LocalDateTime now2,
                                                                               Sort sort);

    List<Booking> findAllByItemAndStatus(Item items, BookingEnum status);

    List<Booking> findAllByItemInAndStatusEquals(List<Item> items, BookingEnum status, Sort sort);

    List<Booking> findAllByItemInAndEndBookingIsBefore(List<Item> items, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemInAndStartBookingIsAfter(List<Item> items, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemInAndStartBookingIsBeforeAndEndBookingIsAfter(List<Item> items,
                                                                             LocalDateTime now,
                                                                             LocalDateTime now2,
                                                                             Sort sort);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);

    List<Booking> findAllByItem(Item item);

}
