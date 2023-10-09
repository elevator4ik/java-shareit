package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(int userId, BookingInfoDto bookingDto) {
        log.info("Start to create booking for item with id {}.", bookingDto.getItemId());
        User booker = getUser(userId);
        Item item = getItemWithCheck(bookingDto.getItemId());
        Booking booking = bookingMapper.toBooking(bookingDto, userId, item);
        boolean crossingCheck = false;

        if (booking.getEndBooking().isAfter(booking.getStartBooking())) {
            if (userId != item.getOwner().getId()) {
                for (Booking b : bookingRepository.findAllByItem(item)
                        .stream()
                        .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                        .collect(Collectors.toList())) {
                    if (isBetween(b.getStartBooking(), booking.getStartBooking(), booking.getEndBooking()) ||
                            isBetween(b.getEndBooking(), booking.getStartBooking(), booking.getEndBooking())) {
                        crossingCheck = true;
                        break;
                    }
                }
                if (crossingCheck) {
                    throw new NotFoundException("Found a crossing of bookings.");
                } else {
                    booking.setBookerId(userId);
                    booking.setStatus(BookingEnum.WAITING);

                    return bookingMapper.toBookingDto(
                            bookingRepository.saveAndFlush(booking), item, booker);
                }
            } else {
                throw new NotFoundException("Owner trying to booking item.");
            }
        } else {
            throw new BadRequestException("End time of booking is equals or before start time.");
        }
    }

    @Override
    public BookingDto approvingBooking(int bookingId, int userId, String approved) {
        log.info("Start to approving booking with id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id "
                        + bookingId + " not found."));
        User booker = getUser(booking.getBookerId());
        Item item = booking.getItem();

        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Not owner trying to update booking status.");
        } else if (booking.getStatus() != BookingEnum.WAITING) {
            throw new BadRequestException("Booking already approved/rejected.");
        }
        switch (approved) {
            case "true":
                booking.setStatus(BookingEnum.APPROVED);
                break;
            case "false":
                booking.setStatus(BookingEnum.REJECTED);
                break;
            default:
                throw new BadRequestException("'Approved' parameter have incorrect value.");
        }
        return bookingMapper.toBookingDto(
                bookingRepository.saveAndFlush(booking), item, booker);
    }

    @Override
    public BookingDto getBooking(int userId, int bookingId) {
        log.info("Start to get booking with id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id "
                        + bookingId + " not found or user not authorized to get this booking."));
        User user = getUser(booking.getBookerId());
        Item item = booking.getItem();

        if (userId == booking.getBookerId() || userId == item.getOwner().getId()) {
            return bookingMapper.toBookingDto(booking, item, user);
        } else {
            throw new NotFoundException("User not authorized to get this booking.");
        }
    }

    @Override
    public List<BookingDto> getBookings(int userId, String state, String userStatus, PageRequest pageRequest) {
        log.info("Start to get booking with user or owner id {} and status {}", userId, state);

        User user = getUser(userId);

        switch (userStatus) {
            case "user":
                log.info("Getting booking with user id {} and status {}", userId, state);

                return getUserBookings(user, state, pageRequest);
            case "owner":
                log.info("Getting booking with owner id {} and status {}", userId, state);

                return getOwnerBookings(user, state, pageRequest);
            default:
                throw new ErrorException("Not valid user status.");
        }
    }

    private List<BookingDto> getUserBookings(User user, String state, PageRequest pageRequest) {
        log.info("Start to get bookings of user {}", user.getId());

        List<Booking> bookings;

        log.info("Start to get bookings with status {}", state);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(user.getId(), pageRequest).toList();

                return bookingMapper.toBookingDtoList(bookings, user);
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBookingIsBefore(user.getId(),
                        LocalDateTime.now(),
                        pageRequest);

                return bookingMapper.toBookingDtoList(bookings, user);
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartBookingIsAfter(user.getId(),
                        LocalDateTime.now(),
                        pageRequest);

                return bookingMapper.toBookingDtoList(bookings, user);
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBookingIsBeforeAndEndBookingIsAfter(user.getId(),
                        LocalDateTime.now(), LocalDateTime.now(),
                        pageRequest);

                return bookingMapper.toBookingDtoList(bookings, user);
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(user.getId(),
                        BookingEnum.WAITING,
                        pageRequest);

                return bookingMapper.toBookingDtoList(bookings, user);
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(user.getId(),
                        BookingEnum.REJECTED,
                        pageRequest);

                return bookingMapper.toBookingDtoList(bookings, user);
            case "APPROVED":
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(user.getId(),
                        BookingEnum.APPROVED,
                        pageRequest);

                return bookingMapper.toBookingDtoList(bookings, user);
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<BookingDto> getOwnerBookings(User user, String state, PageRequest pageRequest) {
        log.info("Start to get bookings of owner {}", user.getId());

        List<Item> items = itemRepository.findAllByOwnerId(user.getId());

        List<Booking> bookings;
        Map<Integer, User> bookers;

        log.info("Start to get bookings with status {}", state);
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllBookingByItemIn(items,
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            case "PAST":
                bookings = bookingRepository.findAllByItemInAndEndBookingIsBefore(items,
                        LocalDateTime.now(),
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            case "FUTURE":
                bookings = bookingRepository.findAllByItemInAndStartBookingIsAfter(items,
                        LocalDateTime.now(),
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            case "CURRENT":
                bookings = bookingRepository.findAllByItemInAndStartBookingIsBeforeAndEndBookingIsAfter(items,
                        LocalDateTime.now(), LocalDateTime.now(),
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            case "WAITING":
                bookings = bookingRepository.findAllByItemInAndStatusEquals(items,
                        BookingEnum.WAITING,
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            case "REJECTED":
                bookings = bookingRepository.findAllByItemInAndStatusEquals(items,
                        BookingEnum.REJECTED,
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            case "APPROVED":
                bookings = bookingRepository.findAllByItemInAndStatusEquals(items,
                        BookingEnum.APPROVED,
                        pageRequest);
                bookers = getBookers(bookings);

                return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Map<Integer, User> getBookers(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return new HashMap<>();
        } else {
            List<Integer> bookersIds = new ArrayList<>();
            for (Booking b : bookings) {
                bookersIds.add(b.getBookerId());
            }
            List<User> users = userRepository.findAllByIdIn(bookersIds);
            Map<Integer, User> userMap = new HashMap<>();
            for (User u : users) {
                userMap.put(u.getId(), u);
            }
            return userMap;
        }
    }

    private User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
    }

    private Item getItemWithCheck(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found."));
        if (item.getAvailable()) {
            return item;
        } else {
            throw new BadRequestException("Item with id " + id + " unavailable.");
        }
    }

    private Boolean isBetween(LocalDateTime date, LocalDateTime before, LocalDateTime after) {
        return date.isAfter(before) && date.isBefore(after);
    }
}
