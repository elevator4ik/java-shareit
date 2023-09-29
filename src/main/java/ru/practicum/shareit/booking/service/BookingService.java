package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(int userId, BookingInfoDto bookingDto);

    BookingDto approvingBooking(int bookingId, int userId, String approved);

    BookingDto getBooking(int userId, int bookingId);

    List<BookingDto> getBookings(int userId, String state, String userStatus);
}
