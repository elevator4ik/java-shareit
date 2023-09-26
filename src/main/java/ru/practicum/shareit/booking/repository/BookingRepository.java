package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByBookerIdAndItemIdAndStatusAndEndBookingBefore(
            Integer userId, Integer itemId, BookingEnum status, LocalDateTime endBooking);

    List<Booking> findAllByBookerIdOrderByStartBookingDesc(int userId);

    List<Booking> findAllByBookerIdAndStartBookingAfterOrderByStartBookingDesc(int bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
            int bookerId, LocalDateTime timeStart, LocalDateTime timeEnd);

    List<Booking> findAllByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(int bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusOrderByStartBookingDesc(int bookerId, BookingEnum state);

    List<Booking> findAllByItemIdInAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
            List<Integer> items, LocalDateTime timeStart, LocalDateTime timeEnd);

    List<Booking> findAllByItemIdInAndStartBookingAfterOrderByStartBookingDesc(List<Integer> items, LocalDateTime time);

    List<Booking> findAllByItemIdInAndEndBookingBeforeOrderByStartBookingDesc(List<Integer> items, LocalDateTime time);

    List<Booking> findAllByItemIdInAndStatusOrderByStartBookingDesc(List<Integer> items, BookingEnum state);

    List<Booking> findAllByItemIdInOrderByStartBookingDesc(List<Integer> items);

}
