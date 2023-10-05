package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking mapper")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig({BookingMapper.class})
public class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;
    private final User user = new User(1,
            "email@mail.com",
            "Name");
    private final User user1 = new User(2,
            "email@mail.com",
            "Name");
    private final Item item = new Item(
            1,
            "item_name",
            "item_description",
            Boolean.TRUE,
            user1,
            null);
    private final Booking booking = new Booking(
            1,
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusHours(1),
            item,
            user.getId(),
            BookingEnum.WAITING);

    @Test
    void toDto() {
        final BookingDto expectedBookingDto = new BookingDto(
                1,
                booking.getStartBooking(),
                booking.getEndBooking(),
                item,
                user,
                BookingEnum.WAITING);
        final BookingDto bookingDto = bookingMapper.toBookingDto(booking, item, user);

        assertEquals(expectedBookingDto.getId(), bookingDto.getId());
        assertEquals(expectedBookingDto.getStart(), bookingDto.getStart());
        assertEquals(expectedBookingDto.getEnd(), bookingDto.getEnd());
        assertEquals(expectedBookingDto.getBooker(), bookingDto.getBooker());
        assertEquals(expectedBookingDto.getItem(), bookingDto.getItem());
        assertEquals(expectedBookingDto.getStatus(), bookingDto.getStatus());
    }

    @Test
    void toBooking() {
        BookingInfoDto bookingDto = new BookingInfoDto(
                booking.getStartBooking(),
                booking.getEndBooking(),
                item.getId());
        final Booking result = bookingMapper.toBooking(bookingDto, user.getId(), item);

        assertNull(result.getId());
        assertEquals(result.getStartBooking(), booking.getStartBooking());
        assertEquals(result.getEndBooking(), booking.getEndBooking());
        assertEquals(result.getBookerId(), booking.getBookerId());
        assertEquals(result.getItem(), booking.getItem());
        assertNull(result.getStatus());
    }

    @Test
    void toBookingDtoList() {
        List<Booking> list = new ArrayList<>();
        list.add(booking);
        List<BookingDto> result = bookingMapper.toBookingDtoList(list, user);

        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(0).getStart(), booking.getStartBooking());
        assertEquals(result.get(0).getEnd(), booking.getEndBooking());
        assertEquals(result.get(0).getBooker(), user);
        assertEquals(result.get(0).getItem(), booking.getItem());
        assertEquals(result.get(0).getStatus(), BookingEnum.WAITING);
    }

    @Test
    void toBookingDtoListFromOwner() {
        Map<Integer, User> map = new HashMap<>();
        List<Booking> list = new ArrayList<>();

        map.put(1, user);
        list.add(booking);

        List<BookingDto> result = bookingMapper.toBookingDtoListFromOwner(list, map);

        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(0).getStart(), booking.getStartBooking());
        assertEquals(result.get(0).getEnd(), booking.getEndBooking());
        assertEquals(result.get(0).getBooker(), user);
        assertEquals(result.get(0).getItem(), booking.getItem());
        assertEquals(result.get(0).getStatus(), BookingEnum.WAITING);
    }
}
