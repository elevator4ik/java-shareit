package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Request mapper")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig({RequestMapper.class})
public class ItemRequestMapperTest {
    @Autowired
    RequestMapper requestMapper;
    private ItemRequestDto incomeDto = new ItemRequestDto();
    private ItemRequestDto expectedDto = new ItemRequestDto(
            1,
            "TestDescription",
            new ArrayList<>(),
            LocalDateTime.now().plusMinutes(1));
    private final User user = new User(1, "user@mail.com", "user");

    @BeforeAll
    void beforeAll() {
        incomeDto.setDescription(expectedDto.getDescription());
    }

    @Test
    void toRequest() {
        ItemRequest expected = new ItemRequest(
                null,
                incomeDto.getDescription(),
                user,
                new ArrayList<>(),
                null);

        ItemRequest result = requestMapper.toRequest(incomeDto, user);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getRequestOwner().toString(), result.getRequestOwner().toString());
        assertEquals(expected.getRequestedItems(), result.getRequestedItems());
    }

    @Test
    void toRequestDto() {
        ItemRequest income = new ItemRequest(
                1,
                incomeDto.getDescription(),
                user,
                List.of(new Item(1, "TestItem", "TestDescription", Boolean.TRUE, user, 1)),
                expectedDto.getCreated());
        ItemRequestDto expected = new ItemRequestDto(
                1,
                incomeDto.getDescription(),
                List.of(new ItemRequestDto.RequestedItem(
                        1,
                        "TestItem",
                        "TestDescription",
                        Boolean.TRUE,
                        1)),
                expectedDto.getCreated());

        ItemRequestDto result = requestMapper.toRequestDto(income);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getItems().get(0).toString(), result.getItems().get(0).toString());
        assertEquals(expected.getCreated(), result.getCreated());
    }
}
