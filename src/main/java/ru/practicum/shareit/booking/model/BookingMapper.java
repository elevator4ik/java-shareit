package ru.practicum.shareit.booking.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public Booking toBooking(BookingInfoDto bookingDto, int userId, Item item) {
        return new Booking(null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                userId,
                null);
    }

    public BookingDto toBookingDto(Booking booking, Item item, User user) {
        return new BookingDto(booking.getId(),
                booking.getStartBooking(),
                booking.getEndBooking(),
                item,
                user,
                booking.getStatus());
    }

    public List<BookingDto> toBookingDtoList(List<Booking> bookings, User user) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        if (!bookings.isEmpty()) {
            for (Booking i : bookings) {
                bookingsDto.add(toBookingDto(i, i.getItem(), user));
            }
        }
        return bookingsDto;
    }

    public List<BookingDto> toBookingDtoListFromOwner(List<Booking> bookings, List<User> users) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        if (!bookings.isEmpty()) {
            for (Booking i : bookings) {
                bookingsDto.add(toBookingDto(i, i.getItem(), users.stream()
                        .filter(u -> u.getId().equals(i.getBookerId()))
                        .collect(Collectors.toList())
                        .get(0)));
            }
        }
        return bookingsDto;
    }
}
