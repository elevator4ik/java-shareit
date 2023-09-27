package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final BookingRepository bookingRepository;
    final BookingMapper bookingMapper;

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
    public List<BookingDto> getBookings(int userId, String state, String userStatus) {
        log.info("Start to get booking with user or owner id {} and status {}", userId, state);

        User user = getUser(userId);

        switch (userStatus) {
            case "user":
                log.info("Getting booking with user id {} and status {}", userId, state);

                return getUserBookings(user, state);
            case "owner":
                log.info("Getting booking with owner id {} and status {}", userId, state);

                return getOwnerBookings(user, state);
            default:
                throw new ErrorException("Not valid user status.");
        }
    }

    private List<BookingDto> getUserBookings(User user, String state) {
        log.info("Start to get bookings of user {}", user.getId());

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartBookingDesc(user.getId());

        if (bookings.isEmpty()) {
            throw new NotFoundException("Bookings of user with id " + user.getId() + "not found.");
        }
        log.info("Start to get bookings with status {}", state);

        if (state.equals(BookingEnum.ALL.toString())) {

            return bookingMapper.toBookingDtoList(bookings, user);
        } else if (state.equals(BookingEnum.PAST.toString())) {

            return bookingMapper.toBookingDtoList(bookings.stream()
                    .filter(b -> b.getEndBooking().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList()), user);
        } else if (state.equals(BookingEnum.FUTURE.toString())) {

            return bookingMapper.toBookingDtoList(bookings.stream()
                    .filter(b -> b.getStartBooking().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList()), user);
        } else if (state.equals(BookingEnum.CURRENT.toString())) {

            return bookingMapper.toBookingDtoList(bookings.stream()
                    .filter(b -> b.getStartBooking().isBefore(LocalDateTime.now()))
                    .filter(b -> b.getEndBooking().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList()), user);
        } else if (state.equals(BookingEnum.WAITING.toString()) ||
                state.equals(BookingEnum.REJECTED.toString()) ||
                state.equals(BookingEnum.APPROVED.toString())) {

            return bookingMapper.toBookingDtoList(bookings.stream()
                    .filter(b -> b.getStatus().toString().equals(state))
                    .collect(Collectors.toList()), user);
        } else {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<BookingDto> getOwnerBookings(User user, String state) {
        log.info("Start to get bookings of owner {}", user.getId());

        List<Item> items = itemRepository.findAllByOwnerId(user.getId());
        if (items.isEmpty()) {
            throw new NotFoundException("Items of owner with id " + user.getId() + "not found.");
        }
        List<Booking> bookings = bookingRepository.findAllByItemInOrderByStartBookingDesc(items);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Bookings of items of owner with id " + user.getId() + "not found.");
        }
        List<Integer> bookersIds = new ArrayList<>();
        for (Booking b : bookings) {
            bookersIds.add(b.getBookerId());
        }
        List<User> bookers = userRepository.findAllByIdIn(bookersIds);

        log.info("Start to get bookings with status {}", state);
        if (state.equals(BookingEnum.ALL.toString())) {

            return bookingMapper.toBookingDtoListFromOwner(bookings, bookers);
        } else if (state.equals(BookingEnum.PAST.toString())) {

            return bookingMapper.toBookingDtoListFromOwner(bookings.stream()
                    .filter(b -> b.getEndBooking().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList()), bookers);
        } else if (state.equals(BookingEnum.FUTURE.toString())) {

            return bookingMapper.toBookingDtoListFromOwner(bookings.stream()
                    .filter(b -> b.getStartBooking().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList()), bookers);
        } else if (state.equals(BookingEnum.CURRENT.toString())) {

            return bookingMapper.toBookingDtoListFromOwner(bookings.stream()
                    .filter(b -> b.getStartBooking().isBefore(LocalDateTime.now()))
                    .filter(b -> b.getEndBooking().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList()), bookers);
        } else if (state.equals(BookingEnum.WAITING.toString()) ||
                state.equals(BookingEnum.REJECTED.toString()) ||
                state.equals(BookingEnum.APPROVED.toString())) {

            return bookingMapper.toBookingDtoListFromOwner(bookings.stream()
                    .filter(b -> b.getStatus().toString().equals(state))
                    .collect(Collectors.toList()), bookers);
        } else {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
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
