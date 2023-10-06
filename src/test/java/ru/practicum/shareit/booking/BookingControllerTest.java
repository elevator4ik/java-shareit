package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("Booking controller")
@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    @Autowired
    private BookingService bookingService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private final User user = new User(1, "test@email.ru", "Test name");
    private final User user1 = new User(2, "test1@email.ru", "Test1 name");
    private final Item item = new Item(
            1,
            "Test Item",
            "Test description",
            Boolean.TRUE,
            user1,
            null);
    private final int from = 0;
    private final int size = 5;
    private final BookingInfoDto incomingDto = new BookingInfoDto(
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            item.getId());

    @Test
    @DisplayName("should return created booking")
    void shouldReturnCreatedBooking() throws Exception {
        BookingDto expectedResult = new BookingDto();

        when(bookingService.addBooking(anyInt(), any()))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(incomingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        BookingDto result = bookingService.addBooking(1, incomingDto);

        assertEquals(result, expectedResult);
    }

    @Test
    @DisplayName("should not return created booking cuz not found")
    void shouldNotCreateNewBookingWhenNotFound() throws Exception {

        when(bookingService.addBooking(anyInt(), any()))
                .thenThrow(new NotFoundException("Some object not found or crossing or owner trying to booking"));

        mockMvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(incomingDto))
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(anyInt(), any()));
    }

    @Test
    @DisplayName("should not return created booking cuz bad request")
    void shouldNotCreateNewBookingWhenEndBeforeStart() throws Exception {

        when(bookingService.addBooking(anyInt(), any()))
                .thenThrow(new BadRequestException("End before start"));

        mockMvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(incomingDto))
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(anyInt(), any()));
    }

    @Test
    @DisplayName("should approve booking")
    void shouldApproveBooking() throws Exception {
        BookingDto expectedResult = new BookingDto(1,
                incomingDto.getStart(),
                incomingDto.getEnd(),
                item,
                user,
                BookingEnum.APPROVED);
        when(bookingService.approvingBooking(anyInt(), anyInt(), anyString()))
                .thenReturn(expectedResult);

        mockMvc.perform(patch("/bookings/{bookingId}", expectedResult.getId())
                .header("X-Sharer-User-Id", 2)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        BookingDto result = bookingService.approvingBooking(anyInt(), anyInt(), anyString());

        assertEquals(result, expectedResult);
    }

    @Test
    @DisplayName("should not approve booking cuz not found")
    void shouldNotApproveBookingNotFound() throws Exception {

        when(bookingService.approvingBooking(anyInt(), anyInt(), anyString()))
                .thenThrow(new NotFoundException("Some object not found or not owner trying to approve"));

        mockMvc.perform(patch("/bookings/{bookingId}", 10000)
                .header("X-Sharer-User-Id", 2)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertThrows(NotFoundException.class, () -> bookingService.approvingBooking(anyInt(), anyInt(), anyString()));

    }

    @Test
    @DisplayName("should not approve booking cuz bad request")
    void shouldNotApproveBookingBadRequest() throws Exception {

        when(bookingService.approvingBooking(anyInt(), anyInt(), anyString()))
                .thenThrow(new BadRequestException("Booking already approved/rejected or parameter have incorrect value"));

        mockMvc.perform(patch("/bookings/{bookingId}", 10000)
                .header("X-Sharer-User-Id", 2)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThrows(BadRequestException.class, () -> bookingService.approvingBooking(anyInt(), anyInt(), anyString()));
    }

    @Test
    @DisplayName("should get booking by id")
    void shouldGetBookingById() throws Exception {
        BookingDto expectedResult = new BookingDto(1,
                incomingDto.getStart(),
                incomingDto.getEnd(),
                item,
                user,
                BookingEnum.APPROVED);

        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings/{bookingId}", expectedResult.getId())
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        BookingDto result = bookingService.getBooking(anyInt(), anyInt());

        assertEquals(result, expectedResult);
    }

    @Test
    @DisplayName("should not get booking by id cuz not found")
    void shouldNotGetBookingById() throws Exception {

        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Booking not found or user not authorized to get this booking"));

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                .header("X-Sharer-User-Id", 1000)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(anyInt(), anyInt()));

    }

    @Test
    @DisplayName("should get bookings of user")
    void shouldGetBookings() throws Exception {

        List<BookingDto> expectedResult = new ArrayList<>();
        when(bookingService.getBookings(anyInt(), anyString(), anyString(), any()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        List<BookingDto> result = bookingService.getBookings(anyInt(), anyString(), anyString(), any());

        assertEquals(result, expectedResult);
    }

    @Test
    @DisplayName("should get bookings of owner")
    void shouldGetOwnerBookings() throws Exception {

        List<BookingDto> expectedResult = new ArrayList<>();
        when(bookingService.getBookings(anyInt(), anyString(), anyString(), any()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 2)
                .param("state", "ALL")
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        List<BookingDto> result = bookingService.getBookings(anyInt(), anyString(), anyString(), any());

        assertEquals(result, expectedResult);
    }

    @Test
    @DisplayName("should get bookings of user cuz bad request")
    void shouldNotGetBookings() throws Exception {

        when(bookingService.getBookings(anyInt(), anyString(), anyString(), any()))
                .thenThrow(new BadRequestException("Unknown state: UNSUPPORTED_STATUS"));

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .param("state", "ALL")
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThrows(BadRequestException.class, () -> bookingService.getBookings(anyInt(), anyString(), anyString(), any()));
    }

    @Test
    @DisplayName("should get bookings of owner  cuz bad request")
    void shouldNotGetOwnerBookings() throws Exception {

        when(bookingService.getBookings(anyInt(), anyString(), anyString(), any()))
                .thenThrow(new BadRequestException("Unknown state: UNSUPPORTED_STATUS"));

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 2)
                .param("state", "ALL")
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertThrows(BadRequestException.class, () -> bookingService.getBookings(anyInt(), anyString(), anyString(), any()));
    }

}