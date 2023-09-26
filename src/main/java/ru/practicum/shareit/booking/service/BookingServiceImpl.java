package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final BookingRepository bookingRepository;
    final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(int userId, BookingInfoDto bookingDto) {
        log.info("Start to create booking for item with id {}.", bookingDto.getItemId());
        User user = getUser(userId);
        Item item = getItemWithCheck(bookingDto.getItemId());
        Booking booking = bookingMapper.toBooking(bookingDto, userId);
        boolean crossingCheck = false;

        if (booking.getEndBooking().isAfter(booking.getStartBooking())) {
            if (userId != item.getOwner().getId()) {
                for (Booking b : item.getBookings()) {
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
                            bookingRepository.saveAndFlush(booking), item, user);
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
        User user = getUser(booking.getBookerId());
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + booking.getItemId() + " not found."));

        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Not owner trying to update booking status.");
        }
        switch (approved) {
            case "true":
                if (booking.getStatus() != null && booking.getStatus().equals(BookingEnum.APPROVED)) {
                    throw new BadRequestException("Booking already approved.");
                }
                booking.setStatus(BookingEnum.APPROVED);
                break;
            case "false":
                booking.setStatus(BookingEnum.REJECTED);
                break;
            default:
                throw new BadRequestException("'Approved' parameter have incorrect value.");
        }
        return bookingMapper.toBookingDto(
                bookingRepository.saveAndFlush(booking), item, user);
    }

    @Override
    public BookingDto getBooking(int userId, int bookingId) {
        log.info("Start to get booking with id {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id "
                        + bookingId + " not found or user not authorized to get this booking."));
        User user = getUser(booking.getBookerId());
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + booking.getItemId() + " not found."));

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
        List<Booking> bookings;
        List<Integer> ids = new ArrayList<>();

        log.info("Start to get bookings with status {}", state);

        if (state.equals(BookingEnum.ALL.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByBookerIdOrderByStartBookingDesc(user.getId()))
                    .orElseThrow(() -> new NotFoundException("Bookings of user with id "
                            + user.getId() + "and param ALL not found."));
            Map<Integer, Item> itemsMap = getItemsMap(bookings, ids);

            return bookingMapper.toBookingDtoList(bookings, itemsMap, user);
        } else if (state.equals(BookingEnum.PAST.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByBookerIdAndEndBookingBeforeOrderByStartBookingDesc(
                                    user.getId(), LocalDateTime.now()))
                    .orElseThrow(() -> new NotFoundException("Bookings of user with id "
                            + user.getId() + "and param PAST not found."));
            Map<Integer, Item> itemsMap = getItemsMap(bookings, ids);

            return bookingMapper.toBookingDtoList(bookings, itemsMap, user);
        } else if (state.equals(BookingEnum.FUTURE.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByBookerIdAndStartBookingAfterOrderByStartBookingDesc(
                                    user.getId(), LocalDateTime.now()))
                    .orElseThrow(() -> new NotFoundException("Bookings of user with id "
                            + user.getId() + "and param FUTURE not found."));
            Map<Integer, Item> itemsMap = getItemsMap(bookings, ids);

            return bookingMapper.toBookingDtoList(bookings, itemsMap, user);
        } else if (state.equals(BookingEnum.CURRENT.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByBookerIdAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
                                    user.getId(), LocalDateTime.now(), LocalDateTime.now()))
                    .orElseThrow(() -> new NotFoundException("Bookings of user with id "
                            + user.getId() + "and param CURRENT not found."));
            Map<Integer, Item> itemsMap = getItemsMap(bookings, ids);

            return bookingMapper.toBookingDtoList(bookings, itemsMap, user);
        } else if (state.equals(BookingEnum.WAITING.toString()) ||
                state.equals(BookingEnum.REJECTED.toString()) ||
                state.equals(BookingEnum.APPROVED.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByBookerIdAndStatusOrderByStartBookingDesc(
                                    user.getId(), BookingEnum.valueOf(state)))
                    .orElseThrow(() -> new NotFoundException("Bookings of user with id "
                            + user.getId() + "and param " + state + " not found."));
            Map<Integer, Item> itemsMap = getItemsMap(bookings, ids);

            return bookingMapper.toBookingDtoList(bookings, itemsMap, user);
        } else {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private List<BookingDto> getOwnerBookings(User user, String state) {
        log.info("Start to get bookings of owner {}", user.getId());

        List<Booking> bookings;
        List<Integer> ids = new ArrayList<>();
        List<Integer> userIds = new ArrayList<>();
        Map<Integer, Item> itemMap = new HashMap<>();
        for (Item i : itemRepository.findAllByOwnerId(user.getId())) {
            ids.add(i.getId());
            itemMap.put(i.getId(), i);
        }
        log.info("Start to get bookings with status {}", state);
        if (state.equals(BookingEnum.ALL.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByItemIdInOrderByStartBookingDesc(ids))
                    .orElseThrow(() -> new NotFoundException("Bookings of owner with id "
                            + user.getId() + "and param ALL not found."));
            Map<Integer, User> usersMap = getUsersMap(bookings, userIds);


            return bookingMapper.toBookingDtoListFromOwner(bookings, itemMap, usersMap);
        } else if (state.equals(BookingEnum.PAST.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByItemIdInAndEndBookingBeforeOrderByStartBookingDesc(
                                    ids, LocalDateTime.now()))
                    .orElseThrow(() -> new NotFoundException("Bookings of owner with id "
                            + user.getId() + "and param PAST not found."));
            Map<Integer, User> usersMap = getUsersMap(bookings, userIds);


            return bookingMapper.toBookingDtoListFromOwner(bookings, itemMap, usersMap);
        } else if (state.equals(BookingEnum.FUTURE.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByItemIdInAndStartBookingAfterOrderByStartBookingDesc(
                                    ids, LocalDateTime.now()))
                    .orElseThrow(() -> new NotFoundException("Bookings of owner with id "
                            + user.getId() + "and param FUTURE not found."));
            Map<Integer, User> usersMap = getUsersMap(bookings, userIds);

            return bookingMapper.toBookingDtoListFromOwner(bookings, itemMap, usersMap);

        } else if (state.equals(BookingEnum.CURRENT.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByItemIdInAndStartBookingBeforeAndEndBookingAfterOrderByStartBookingDesc(
                                    ids, LocalDateTime.now(), LocalDateTime.now()))
                    .orElseThrow(() -> new NotFoundException("Bookings of owner with id "
                            + user.getId() + "and param CURRENT not found."));
            Map<Integer, User> usersMap = getUsersMap(bookings, userIds);

            return bookingMapper.toBookingDtoListFromOwner(bookings, itemMap, usersMap);
        } else if (state.equals(BookingEnum.WAITING.toString()) ||
                state.equals(BookingEnum.REJECTED.toString()) ||
                state.equals(BookingEnum.APPROVED.toString())) {

            bookings = Optional.of(
                            bookingRepository.findAllByItemIdInAndStatusOrderByStartBookingDesc(ids, BookingEnum.valueOf(state)))
                    .orElseThrow(() -> new NotFoundException("Bookings of owner with id "
                            + user.getId() + "and param " + state + " not found."));
            Map<Integer, User> usersMap = getUsersMap(bookings, userIds);


            return bookingMapper.toBookingDtoListFromOwner(bookings, itemMap, usersMap);
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

    private Map<Integer, Item> getItemsMap(List<Booking> bookings, List<Integer> ids) {
        for (Booking b : bookings) {
            ids.add(b.getItemId());
        }
        List<Item> items = itemRepository.findAllByIdIn(ids);
        Map<Integer, Item> itemsMap = new HashMap<>();
        for (Item i : items) {
            itemsMap.put(i.getId(), i);
        }
        return itemsMap;
    }

    private Map<Integer, User> getUsersMap(List<Booking> bookings, List<Integer> userIds) {
        for (Booking b : bookings) {
            userIds.add(b.getBookerId());
        }
        Map<Integer, User> usersMap = new HashMap<>();
        for (User u : userRepository.findAllByIdIn(userIds)) {
            usersMap.put(u.getId(), u);
        }
        return usersMap;
    }

    public static Boolean isBetween(LocalDateTime date, LocalDateTime before, LocalDateTime after) {
        return date.isAfter(before) && date.isBefore(after);
    }
}
