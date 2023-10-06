package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findFirstByItemAndBookerIdAndStatusAndEndBookingBeforeOrderByStartBooking(
            Item item, Integer bookerId, BookingEnum status, LocalDateTime now);

    Page<Booking> findByBookerId(int userId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusEquals(int userId, BookingEnum state, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBookingIsBefore(int userId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartBookingIsAfter(int userId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartBookingIsBeforeAndEndBookingIsAfter(int userId,
                                                                               LocalDateTime now,
                                                                               LocalDateTime now2,
                                                                               PageRequest pageRequest);

    List<Booking> findAllByItemAndStatus(Item items, BookingEnum status);

    List<Booking> findAllByItemInAndStatusEquals(List<Item> items, BookingEnum status, PageRequest pageRequest);

    List<Booking> findAllByItemInAndEndBookingIsBefore(List<Item> items, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findAllByItemInAndStartBookingIsAfter(List<Item> items, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findAllByItemInAndStartBookingIsBeforeAndEndBookingIsAfter(List<Item> items,
                                                                             LocalDateTime now,
                                                                             LocalDateTime now2,
                                                                             PageRequest pageRequest);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);

    List<Booking> findAllBookingByItemIn(List<Item> items, PageRequest pageRequest);

    List<Booking> findAllByItem(Item item);

}
