package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item mapper")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig({ItemMapper.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;
    private User user = new User(1, "test@email.ru", "Test name");
    private User user1 = new User(2, "test1@email.ru", "Test1 name");
    private Item item = new Item(
            1, "TestItem", "TestDescription", Boolean.TRUE, user, null);
    private ItemDto incomeDto;
    private ItemDto expectedDto;
    private Booking nextBooking;
    private Booking lastBooking;
    private ItemDto.ItemBookingDto nextBookingDto;
    private ItemDto.ItemBookingDto lastBookingDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeAll
    void beforeAll() {
        nextBooking = new Booking(
                1,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                item,
                user1.getId(),
                BookingEnum.APPROVED);
        lastBooking = new Booking(
                2,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                item,
                user1.getId(),
                BookingEnum.APPROVED);
        comment = new Comment(1, LocalDateTime.now(), "text", user1, item);
        commentDto = new CommentDto(1, "text", user1.getName(), comment.getCreated());
        nextBookingDto = new ItemDto.ItemBookingDto(
                1,
                nextBooking.getStartBooking(),
                nextBooking.getEndBooking(),
                user1.getId());
        lastBookingDto = new ItemDto.ItemBookingDto(
                2,
                lastBooking.getStartBooking(),
                lastBooking.getEndBooking(),
                user1.getId());
        incomeDto = new ItemDto();
        incomeDto.setName("TestItem");
        incomeDto.setDescription("TestDescription");
        incomeDto.setAvailable(Boolean.TRUE);
        expectedDto = new ItemDto();
        expectedDto.setId(1);
        expectedDto.setName("TestItem");
        expectedDto.setDescription("TestDescription");
        expectedDto.setAvailable(Boolean.TRUE);
        expectedDto.setNextBooking(nextBookingDto);
        expectedDto.setLastBooking(lastBookingDto);
        expectedDto.setComments(List.of(commentDto));
    }

    @Test
    @Order(1)
    void toItem() {
        Item expected = new Item(
                null, "TestItem", "TestDescription", Boolean.TRUE, user, null);
        Item result = itemMapper.toItem(incomeDto, user);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getAvailable(), result.getAvailable());
        assertEquals(expected.getOwner(), result.getOwner());
        assertEquals(expected.getAvailable(), result.getAvailable());

    }

    @Test
    @Order(2)
    void toItemDto() {
        ItemDto result = itemMapper.toItemDto(item, List.of(comment), nextBooking, lastBooking);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getDescription(), result.getDescription());
        assertEquals(expectedDto.getAvailable(), result.getAvailable());
        assertEquals(expectedDto.getNextBooking().toString(), result.getNextBooking().toString());
        assertEquals(expectedDto.getLastBooking().toString(), result.getLastBooking().toString());
        assertEquals(expectedDto.getComments().get(0).toString(), result.getComments().get(0).toString());
    }

    @Test
    @Order(3)
    void toItemDtoList() {
        List<ItemDto> result = itemMapper.toItemDtoList(
                List.of(item), Map.of(1, nextBooking), Map.of(1, lastBooking), Map.of(1, List.of(comment)));

        assertEquals(expectedDto.toString(), result.get(0).toString());
    }

    @Test
    @Order(4)
    void toItemDtoForUser() {
        ItemDto newExpected = expectedDto;
        newExpected.setNextBooking(null);
        newExpected.setLastBooking(null);

        ItemDto result = itemMapper.toItemDtoForUser(item, List.of(comment));

        assertEquals(newExpected.toString(), result.toString());
    }

    @Test
    @Order(5)
    void toItemDtoListForUser() {
        ItemDto newExpected = expectedDto;
        newExpected.setNextBooking(null);
        newExpected.setLastBooking(null);
        List<ItemDto> result = itemMapper.toItemDtoListForUser(
                List.of(item), Map.of(1, List.of(comment)));

        assertEquals(newExpected.toString(), result.get(0).toString());
    }
}
