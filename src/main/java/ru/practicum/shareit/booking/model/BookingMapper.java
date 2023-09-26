package ru.practicum.shareit.booking.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BookingMapper {

    public Booking toBooking(BookingInfoDto bookingDto, int userId) {
        return new Booking(null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
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

    public List<BookingDto> toBookingDtoList(List<Booking> bookings, Map<Integer, Item> items, User user) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking i : bookings) {
            bookingsDto.add(toBookingDto(i, items.get(i.getItemId()), user));
        }
        return bookingsDto;
    }

    public List<BookingDto> toBookingDtoListFromOwner(List<Booking> bookings, Map<Integer, Item> items,
                                                      Map<Integer, User> users) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking i : bookings) {
            bookingsDto.add(toBookingDto(i, items.get(i.getItemId()), users.get(i.getBookerId())));
        }
        return bookingsDto;
    }
}
