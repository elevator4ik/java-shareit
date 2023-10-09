package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @Validated({Create.class})
                                                @RequestBody BookingInfoDto bookingDto) {
        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @PathVariable Integer bookingId,
                                                 @RequestParam("approved") Boolean approved) {
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int bookingId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(name = "size", defaultValue = "1000") @Min(1) Integer size,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {

        return bookingClient.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(name = "size", defaultValue = "1000") @Min(1) Integer size,
                                                   @RequestParam(name = "state",
                                                           defaultValue = "ALL") String state) {

        return bookingClient.getOwnerBookings(state, userId, from, size);
    }
}
