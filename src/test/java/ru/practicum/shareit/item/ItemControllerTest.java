package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Item controller")
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ItemControllerTest {
    @MockBean
    @Autowired
    private ItemService itemService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto incomeDto;
    private ItemDto expectedDto;

    @BeforeAll
    void beforeAll() {
        expectedDto = new ItemDto(
                1,
                "TestItem",
                "TestDescription",
                Boolean.TRUE,
                null,
                null,
                null,
                null);

        incomeDto = new ItemDto();
        incomeDto.setId(1);
        incomeDto.setName("TestItem");
        incomeDto.setDescription("TestDescription");
        incomeDto.setAvailable(Boolean.TRUE);
    }

    @Test
    @DisplayName("Item create")
    void shouldCreateItem() throws Exception {
        when(itemService.createItem(anyInt(), any()))
                .thenReturn(expectedDto);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(incomeDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedDto)));

        ItemDto result = itemService.createItem(1, incomeDto);

        assertEquals(result, expectedDto);
    }

    @Test
    @DisplayName("Item update")
    void shouldUpdateItem() throws Exception {
        when(itemService.updateItem(anyInt(), anyInt(), any()))
                .thenReturn(expectedDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(incomeDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedDto)));

        ItemDto result = itemService.updateItem(1, 1, incomeDto);

        assertEquals(result, expectedDto);
    }

    @Test
    @DisplayName("Item get")
    void shouldGetItem() throws Exception {
        when(itemService.getItem(anyInt(), anyInt()))
                .thenReturn(expectedDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedDto)));

        ItemDto result = itemService.getItem(1, 1);

        assertEquals(result, expectedDto);
    }

    @Test
    @DisplayName("Items get by owner")
    void shouldGetItems() throws Exception {
        List<ItemDto> list = List.of(expectedDto);
        when(itemService.getItemsOfOwner(anyInt()))
                .thenReturn(list);

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));

        List<ItemDto> result = itemService.getItemsOfOwner(1);

        assertEquals(result, list);
    }

    @Test
    @DisplayName("Items search")
    void shouldSearchItems() throws Exception {
        List<ItemDto> list = List.of(expectedDto);
        when(itemService.searchItem(anyInt(), anyString()))
                .thenReturn(list);

        mockMvc.perform(get("/items/search")
                .header("X-Sharer-User-Id", 1)
                .param("text", "text")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));

        List<ItemDto> result = itemService.searchItem(anyInt(), anyString());

        assertEquals(result, list);
    }

    @Test
    @DisplayName("Comment add")
    void shouldCreateComment() throws Exception {
        CommentDto incomeComment = new CommentDto();
        incomeComment.setText("text");
        CommentDto expectedComment = new CommentDto();

        when(itemService.createComment(anyInt(), anyInt(), any()))
                .thenReturn(expectedComment);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(incomeComment))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedComment)));

        CommentDto result = itemService.createComment(1, 1, incomeComment);

        assertEquals(result, expectedComment);
    }
}
