package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking service")
@TestInstance(Lifecycle.PER_CLASS)
public class BookingsServiceTest {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private BookingMapper bookingMapper;
    BookingServiceImpl bookingService;
    private final User user = new User(1, "test@email.ru", "Test name");
    private final User user1 = new User(2, "test1@email.ru", "Test1 name");
    private final Item item = new Item(
            1, "Test Item", "Test description", Boolean.TRUE, user1, null);
    private final int from = 0;
    private final int size = 5;
    private final PageRequest pageRequest = new MyPageRequest(from, size, Sort.by(
            Sort.Direction.DESC, "startBooking"));

    @BeforeEach
    void before() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("should add booking")
    void shouldAddBooking() {
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        final BookingDto expectedDto = new BookingDto(1,
                incomingDto.getStart(),
                incomingDto.getEnd(),
                item,
                user,
                BookingEnum.WAITING);
        final Booking booking = new Booking(1,
                incomingDto.getStart(),
                incomingDto.getEnd(),
                item,
                user.getId(),
                BookingEnum.WAITING);

        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(item));
        when(bookingMapper.toBooking(any(), anyInt(), any()))
                .thenReturn(booking);
        when(bookingRepository.saveAndFlush(booking))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(any(), any(), any()))
                .thenReturn(expectedDto);
        BookingDto bookingDto = bookingService.addBooking(user.getId(), incomingDto);

        assertEquals(expectedDto, bookingDto);

    }

    @Test
    @DisplayName("should not add booking cuz bad request")
    void shouldNotAddBooking() {
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        final Booking booking = new Booking(1,
                incomingDto.getEnd(),
                incomingDto.getStart(),
                item,
                user.getId(),
                BookingEnum.WAITING);

        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(item));
        when(bookingMapper.toBooking(any(), anyInt(), any()))
                .thenReturn(booking);

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(user.getId(), incomingDto));
    }

    @Test
    @DisplayName("should not add booking cuz not found user")
    void shouldNotAddBooking2() {
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());

        when(userRepository.findById(anyInt()))
                .thenThrow(new NotFoundException("User not found."));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), incomingDto));
    }

    @Test
    @DisplayName("should not add booking cuz not found item")
    void shouldNotAddBooking3() {
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());

        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenThrow(new NotFoundException("Item not found."));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), incomingDto));
    }

    @Test
    @DisplayName("should not add booking cuz bad request item")
    void shouldNotAddBooking4() {
        final Item item1 = new Item(
                1, "Test Item", "Test description", Boolean.FALSE, user1, null);
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());

        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(item1));

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(user.getId(), incomingDto));
    }

    @Test
    @DisplayName("should not add booking cuz crossingCheck false")
    void shouldNotAddBooking5() {
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        final Booking booking = new Booking(1,
                incomingDto.getStart(),
                incomingDto.getEnd(),
                item,
                user.getId(),
                BookingEnum.WAITING);
        final Booking bookingOld = new Booking(2,
                incomingDto.getStart().plusMinutes(1),
                incomingDto.getEnd().plusHours(1),
                item,
                user.getId(),
                BookingEnum.APPROVED);
        final List<Booking> list = new ArrayList<>();

        list.add(bookingOld);

        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(item));
        when(bookingMapper.toBooking(any(), anyInt(), any()))
                .thenReturn(booking);
        when(bookingRepository.findAllByItem(item))
                .thenReturn(list);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), incomingDto));
    }

    @Test
    @DisplayName("should not add booking cuz owner trying to booking")
    void shouldNotAddBooking6() {
        final Item item1 = new Item(
                1, "Test Item", "Test description", Boolean.TRUE, user, null);
        final BookingInfoDto incomingDto = new BookingInfoDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        final Booking booking = new Booking(1,
                incomingDto.getStart(),
                incomingDto.getEnd(),
                item,
                user.getId(),
                BookingEnum.WAITING);

        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(item1));
        when(bookingMapper.toBooking(any(), anyInt(), any()))
                .thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(user.getId(), incomingDto));
    }

    @Test
    @DisplayName("should approve booking")
    void shouldApproveBooking() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        final Booking bookingApproved = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.APPROVED);
        final BookingDto expectedDto = new BookingDto(1,
                booking.getStartBooking(),
                booking.getEndBooking(),
                item,
                user,
                BookingEnum.APPROVED);
        final String approved = "true";

        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.saveAndFlush(booking))
                .thenReturn(bookingApproved);
        when(bookingMapper.toBookingDto(bookingApproved, booking.getItem(), user))
                .thenReturn(expectedDto);
        assertEquals(bookingService.approvingBooking(booking.getId(), user1.getId(), approved), expectedDto);
    }

    @Test
    @DisplayName("should reject booking")
    void shouldRejectBooking() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        final Booking bookingRejected = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.REJECTED);
        final BookingDto expectedDto = new BookingDto(1,
                booking.getStartBooking(),
                booking.getEndBooking(),
                item,
                user,
                BookingEnum.REJECTED);
        final String approved = "false";

        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.saveAndFlush(booking))
                .thenReturn(bookingRejected);
        when(bookingMapper.toBookingDto(bookingRejected, booking.getItem(), user))
                .thenReturn(expectedDto);
        assertEquals(bookingService.approvingBooking(booking.getId(), user1.getId(), approved), expectedDto);
    }

    @Test
    @DisplayName("should not approve booking cuz not found booking")
    void shouldNotApproveBooking() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        final String approved = "true";

        when(bookingRepository.findById(any()))
                .thenThrow(new NotFoundException("Booking with id " + booking.getId() + " not found."));

        assertThrows(NotFoundException.class, () -> bookingService.approvingBooking(booking.getId(), user1.getId(), approved));
    }

    @Test
    @DisplayName("should not approve booking cuz not owner trying")
    void shouldNotApproveBooking1() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        final String approved = "true";

        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));

        assertThrows(NotFoundException.class, () -> bookingService.approvingBooking(booking.getId(), user.getId(), approved));
    }

    @Test
    @DisplayName("should not approve booking cuz approved")
    void shouldNotApproveBooking2() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.APPROVED);
        final String approved = "true";

        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user1));

        assertThrows(BadRequestException.class, () -> bookingService.approvingBooking(booking.getId(), user1.getId(), approved));
    }

    @Test
    @DisplayName("should not approve booking cuz approve param false")
    void shouldNotApproveBooking3() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.ALL);
        final String approved = "tribute";

        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));

        assertThrows(BadRequestException.class, () -> bookingService.approvingBooking(booking.getId(), user1.getId(), approved));
    }

    @Test
    @DisplayName("should get booking")
    void shouldGetBooking() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        final BookingDto expectedDto = new BookingDto(1,
                booking.getStartBooking(),
                booking.getEndBooking(),
                item,
                user,
                BookingEnum.WAITING);

        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingMapper.toBookingDto(booking, item, user))
                .thenReturn(expectedDto);
        assertEquals(bookingService.getBooking(user.getId(), booking.getId()), expectedDto);
    }

    @Test
    @DisplayName("should not get booking cuz not found")
    void shouldNotGetBooking() {
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), 100));
    }

    @Test
    @DisplayName("should not get booking cuz not owner/booker")
    void shouldNotGetBooking1() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        when(bookingRepository.findById(any()))
                .thenReturn(java.util.Optional.of(booking));
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new User(3, "test@email.ru", "Test name")));
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(3, booking.getId()));
    }

    @Test
    @DisplayName("should not get bookings cuz not valid user status ")
    void shouldNotGetBookings0() {
        String userStatus = "trombone";
        String state = "ALL";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));

        assertThrows(ErrorException.class,
                () -> bookingService.getBookings(user.getId(), state, userStatus, pageRequest));

    }

    @Test
    @DisplayName("should get ALL bookings of user")
    void shouldGetBookings() {

        final Page<Booking> pageBookings = new PageImpl<>(new ArrayList<>());
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "ALL";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findByBookerId(anyInt(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get PAST bookings of user")
    void shouldGetBookings2() {

        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "PAST";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndBookingIsBefore(anyInt(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get FUTURE bookings of user")
    void shouldGetBookings3() {

        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "FUTURE";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartBookingIsAfter(anyInt(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get CURRENT bookings of user")
    void shouldGetBookings4() {

        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "CURRENT";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartBookingIsBeforeAndEndBookingIsAfter(anyInt(), any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get WAITING bookings of user")
    void shouldGetBookings5() {

        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "WAITING";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyInt(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get REJECTED bookings of user")
    void shouldGetBookings6() {

        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "REJECTED";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyInt(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get APPROVED bookings of user")
    void shouldGetBookings7() {

        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "user";
        String state = "APPROVED";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyInt(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoList(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should not get bookings of user cuz UNSUPPORTED_STATUS")
    void shouldNotGetBookings() {
        String userStatus = "user";
        String state = "UNSUPPORTED_STATUS";

        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        assertThrows(BadRequestException.class,
                () -> bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get ALL bookings of owner")
    void shouldGetOwnerBookings() {
        final Booking booking = new Booking(1,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item,
                user.getId(),
                BookingEnum.WAITING);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<User> users = new ArrayList<>();
        users.add(user1);
        List<Booking> pageBookings = new ArrayList<>();
        pageBookings.add(booking);
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "ALL";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllBookingByItemIn(any(), any()))
                .thenReturn(pageBookings);
        when(userRepository.findAllByIdIn(any()))
                .thenReturn(users);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get PAST bookings of owner")
    void shouldGetOwnerBookings1() {
        List<Item> items = new ArrayList<>();
        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "PAST";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemInAndEndBookingIsBefore(any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get FUTURE bookings of owner")
    void shouldGetOwnerBookings2() {
        List<Item> items = new ArrayList<>();
        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "FUTURE";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemInAndStartBookingIsAfter(any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get CURRENT bookings of owner")
    void shouldGetOwnerBookings3() {
        List<Item> items = new ArrayList<>();
        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "CURRENT";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemInAndStartBookingIsBeforeAndEndBookingIsAfter(any(), any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get WAITING bookings of owner")
    void shouldGetOwnerBookings4() {
        List<Item> items = new ArrayList<>();
        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "WAITING";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusEquals(any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get REJECTED bookings of owner")
    void shouldGetOwnerBookings5() {
        List<Item> items = new ArrayList<>();
        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "REJECTED";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusEquals(any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should get APPROVED bookings of owner")
    void shouldGetOwnerBookings6() {
        List<Item> items = new ArrayList<>();
        final List<Booking> pageBookings = new ArrayList<>();
        final List<BookingDto> expectedBookings = new ArrayList<>();
        String userStatus = "owner";
        String state = "APPROVED";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatusEquals(any(), any(), any()))
                .thenReturn(pageBookings);
        when(bookingMapper.toBookingDtoListFromOwner(any(), any()))
                .thenReturn(expectedBookings);
        assertEquals(expectedBookings, bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }

    @Test
    @DisplayName("should not get bookings of owner cuz UNSUPPORTED_STATUS")
    void shouldNotGetOwnerBookings() {
        List<Item> items = new ArrayList<>();
        String userStatus = "owner";
        String state = "UNSUPPORTED_STATUS";
        when(userRepository.findById(any()))
                .thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(items);
        assertThrows(BadRequestException.class,
                () -> bookingService.getBookings(user.getId(), state, userStatus, pageRequest));
    }
}
