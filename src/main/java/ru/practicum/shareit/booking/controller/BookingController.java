package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.marker.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @Validated({Create.class})
                                    @RequestBody BookingInfoDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @PathVariable int bookingId,
                                     @RequestParam("approved") String approved) {
        return bookingService.approvingBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                            @RequestParam(name = "state",
                                                    required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookings(userId, state, "user");
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestParam(name = "state",
                                                     defaultValue = "ALL") String state) {
        return bookingService.getBookings(userId, state, "owner");
    }
}
