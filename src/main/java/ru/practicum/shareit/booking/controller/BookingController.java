package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.MyPageRequest;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
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
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(name = "size", defaultValue = "1000") @Min(1) Integer size,
                                            @RequestParam(name = "state",defaultValue = "ALL") String state) {
        final PageRequest pageRequest = new MyPageRequest(from / size, size, Sort.by(
                Sort.Direction.DESC, "startBooking"));
        return bookingService.getBookings(userId, state, "user", pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(name = "size", defaultValue = "1000") @Min(1) Integer size,
                                             @RequestParam(name = "state",
                                                     defaultValue = "ALL") String state) {
        final PageRequest pageRequest = new MyPageRequest(from / size, size, Sort.by(
                Sort.Direction.DESC, "startBooking"));
        return bookingService.getBookings(userId, state, "owner", pageRequest);
    }
}
