package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Item request controller")
@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ItemRequestControllerTest {
    @MockBean
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemRequestDto incomeDto = new ItemRequestDto();
    private ItemRequestDto expectedDto;

    @BeforeAll
    void beforeAll() {
        expectedDto = new ItemRequestDto(
                1,
                "TestDescription",
                null,
                LocalDateTime.now().plusMinutes(1));
        incomeDto.setDescription(expectedDto.getDescription());
    }

    @Test
    @DisplayName("Request create")
    void createRequest() throws Exception {
        when(itemRequestService.createRequest(anyInt(),any()))
                .thenReturn(expectedDto);

        mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(incomeDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedDto)));

        ItemRequestDto result = itemRequestService.createRequest(1, incomeDto);

        assertEquals(result, expectedDto);
    }

    @Test
    @DisplayName("Get user requests")
    void getUserRequests() throws Exception {
        when(itemRequestService.getRequests(anyInt(),any(), anyString()))
                .thenReturn(List.of(expectedDto));

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(expectedDto))));

        List<ItemRequestDto> result = itemRequestService.getRequests(1, null, "owner");

        assertEquals(result, List.of(expectedDto));
    }

    @Test
    @DisplayName("Get all requests")
    void getAllRequests() throws Exception {
        when(itemRequestService.getRequests(anyInt(),any(), anyString()))
                .thenReturn(List.of(expectedDto));

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(expectedDto))));

        List<ItemRequestDto> result = itemRequestService.getRequests(1, null, "user");

        assertEquals(result, List.of(expectedDto));
    }

    @Test
    @DisplayName("Get request")
    void getRequest() throws Exception {
        when(itemRequestService.getRequest(anyInt(),any()))
                .thenReturn(expectedDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedDto)));

        ItemRequestDto result = itemRequestService.getRequest(1, 1);

        assertEquals(result, expectedDto);
    }
}
