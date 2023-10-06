package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Item service")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceTest {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentsRepository commentsRepository;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemService itemService;
    private User user = new User(1, "test@email.ru", "Test name");
    private User user1 = new User(2, "test1@email.ru", "Test1 name");
    private User user2 = new User(3, "test2@email.ru", "Test2 name");
    private Item item = new Item(
            1, "Test Item", "Test description", Boolean.TRUE, user, null);
    private ItemDto incomeDto;
    private ItemDto expectedDto;
    Booking nextBooking;
    Booking lastBooking;
    ItemDto.ItemBookingDto nextBookingDto;
    ItemDto.ItemBookingDto lastBookingDto;
    Comment comment;
    CommentDto commentDto;

    @BeforeAll
    void beforeAll() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentsRepository = mock(CommentsRepository.class);
        itemMapper = mock(ItemMapper.class);
        commentMapper = mock(CommentMapper.class);
        itemService = new ItemServiceImpl(
                commentsRepository, bookingRepository, userRepository, itemRepository, itemMapper, commentMapper);
        incomeDto = new ItemDto();
        incomeDto.setName("TestItem");
        incomeDto.setDescription("TestDescription");
        incomeDto.setAvailable(Boolean.TRUE);
        expectedDto = new ItemDto();
        expectedDto.setId(1);
        expectedDto.setName("TestItem");
        expectedDto.setDescription("TestDescription");
        expectedDto.setAvailable(Boolean.TRUE);

        nextBooking = new Booking(
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item, user1.getId(),
                BookingEnum.APPROVED);
        lastBooking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                item, user1.getId(),
                BookingEnum.APPROVED);

        nextBookingDto = new ItemDto.ItemBookingDto(
                1,
                nextBooking.getStartBooking(),
                nextBooking.getEndBooking(),
                user1.getId());
        lastBookingDto = new ItemDto.ItemBookingDto(
                1,
                lastBooking.getStartBooking(),
                lastBooking.getEndBooking(),
                user1.getId());
        comment = new Comment(1, LocalDateTime.now(), "text", user1, item);
        commentDto = new CommentDto(1, "text", user1.getName(), LocalDateTime.now());
    }

    @Test
    @DisplayName("should add item")
    void shouldAddItem() {

        when(itemMapper.toItem(any(), any()))
                .thenReturn(item);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(commentsRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        when(itemRepository.saveAndFlush(any()))
                .thenReturn(item);
        when(itemMapper.toItemDtoForUser(any(), any()))
                .thenReturn(expectedDto);

        ItemDto result = itemService.createItem(user.getId(), incomeDto);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("should not add item cuz not found")
    void shouldNotAddItem() {

        when(itemMapper.toItem(any(), any()))
                .thenReturn(item);
        when(userRepository.findById(user.getId()))
                .thenThrow(new NotFoundException("user not found"));

        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), incomeDto));
    }

    @Test
    @DisplayName("should update item")
    void shouldUpdateItem() {
        ItemDto newIncomingDto = new ItemDto();
        newIncomingDto.setName("NewName");
        newIncomingDto.setDescription("NewDesc");
        newIncomingDto.setAvailable(Boolean.FALSE);
        ItemDto newExpectedDto = expectedDto;
        newExpectedDto.setName("NewName");
        newExpectedDto.setDescription("NewDesc");
        newExpectedDto.setAvailable(Boolean.FALSE);

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(commentsRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        when(itemMapper.toItemDto(any(), any(), any(), any()))
                .thenReturn(newExpectedDto);

        ItemDto result = itemService.updateItem(1, 1, newIncomingDto);
        assertEquals(newExpectedDto, result);
    }

    @Test
    @DisplayName("should not update item cuz not owner")
    void shouldNotUpdateItem() {

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1, 3, incomeDto));
    }

    @Test
    @DisplayName("should not update item cuz not found")
    void shouldNotUpdateItem2() {

        assertThrows(NotFoundException.class, () -> itemService.updateItem(2, 3, incomeDto));
    }

    @Test
    @DisplayName("should get item")
    void shouldGetItem() {
        ItemDto newExpectedDto = expectedDto;
        newExpectedDto.setName("NewName");
        newExpectedDto.setDescription("NewDesc");
        newExpectedDto.setAvailable(Boolean.FALSE);
        newExpectedDto.setLastBooking(lastBookingDto);
        newExpectedDto.setNextBooking(nextBookingDto);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(commentsRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemAndStatus(any(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(itemMapper.toItemDto(any(), any(), any(), any()))
                .thenReturn(expectedDto);

        ItemDto result = itemService.getItem(1, 1);
        assertEquals(newExpectedDto, result);
    }

    @Test
    @DisplayName("should get item no bookings")
    void shouldGetItemWithoutBookings() {

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(commentsRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        when(itemMapper.toItemDto(any(), any(), any(), any()))
                .thenReturn(expectedDto);

        ItemDto result = itemService.getItem(1, 1);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("should get item for user")
    void shouldGetItemForUser() {

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(commentsRepository.findAllByItem(any()))
                .thenReturn(List.of(comment));
        when(itemMapper.toItemDtoForUser(any(), any()))
                .thenReturn(expectedDto);

        ItemDto result = itemService.getItem(1, 2);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("should get item for owner")
    void shouldGetItemForOwner() {

        when(itemRepository.findAllByOwnerId(anyInt()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIn(any(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentsRepository.findAllByItemIn(any()))
                .thenReturn(List.of(comment));
        when(itemMapper.toItemDtoList(any(), any(), any(), any()))
                .thenReturn(List.of(expectedDto));

        List<ItemDto> result = itemService.getItemsOfOwner(1);
        assertEquals(List.of(expectedDto), result);
    }

    @Test
    @DisplayName("should search item")
    void shouldSearchItem() {
        String text = "Test";

        when(itemRepository.search(anyString()))
                .thenReturn((List.of(item)));
        when(commentsRepository.findAllByItemIn(any()))
                .thenReturn(List.of(comment));
        when(itemMapper.toItemDtoListForUser(any(), any()))
                .thenReturn(List.of(expectedDto));

        List<ItemDto> result = itemService.searchItem(1, text);
        assertEquals(List.of(expectedDto), result);
    }

    @Test
    @DisplayName("should create comment")
    void shouldCreateComment() {
        Booking thisBooking = new Booking(
                1,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(1),
                item,
                user1.getId(),
                BookingEnum.APPROVED);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemAndBookerIdAndStatusAndEndBookingBeforeOrderByStartBooking(
                any(), anyInt(), any(), any()))
                .thenReturn(Optional.of(thisBooking));
        when(commentMapper.toComment(any(), any(), any()))
                .thenReturn(comment);
        when(commentsRepository.saveAndFlush(any()))
                .thenReturn(comment);
        when(commentMapper.toCommentDto(any()))
                .thenReturn(commentDto);

        CommentDto result = itemService.createComment(2, 1, commentDto);
        assertEquals(commentDto, result);
    }
}