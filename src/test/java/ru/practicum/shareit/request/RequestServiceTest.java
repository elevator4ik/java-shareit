package ru.practicum.shareit.request;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Request service")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestServiceTest {
    private ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private RequestMapper requestMapper = mock(RequestMapper.class);
    @InjectMocks
    private ItemRequestService requestService = new ItemRequestServiceImpl(
            itemRequestRepository, userRepository, requestMapper);
    private User user = new User(1, "test@email.ru", "Test name");
    private User user1 = new User(2, "test1@email.ru", "Test1 name");
    private Item item = new Item(
            1, "Test Item", "Test description", Boolean.TRUE, user, null);

    @Test
    @DisplayName("should create request")
    @Order(5)
    void shouldCreateRequest() {
        ItemRequestDto expected = new ItemRequestDto(null, "Test", null, LocalDateTime.now());

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(requestMapper.toRequest(any(), any()))
                .thenReturn(new ItemRequest());
        when(itemRequestRepository.saveAndFlush(any()))
                .thenReturn(new ItemRequest());
        when(requestMapper.toRequestDto(any()))
                .thenReturn(expected);

        ItemRequestDto result = requestService.createRequest(1, expected);

        assertEquals(result, expected);
    }

    @Test
    @DisplayName("should not create request cuz not found user")
    @Order(6)
    void shouldNotCreateRequest() {
        when(userRepository.findById(anyInt()))
                .thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> requestService.createRequest(1, new ItemRequestDto()));
    }

    @Test
    @DisplayName("should get requests for owner")
    @Order(1)
    void shouldGetRequests() {

        ItemRequestDto expected = new ItemRequestDto(1, "Test", null, LocalDateTime.now());
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRequestRepository.findAllByRequestOwner(any(), any()))
                .thenReturn(List.of(new ItemRequest()));
        when(requestMapper.toRequestDto(any()))
                .thenReturn(expected);

        List<ItemRequestDto> result = requestService.getRequests(2, null, "owner");

        assertEquals(result.toString(), List.of(expected).toString());
    }

    @Test
    @DisplayName("should get requests for user")
    @Order(2)
    void shouldGetRequests2() {

        ItemRequestDto expected = new ItemRequestDto(1,
                "Test",
                List.of(new ItemRequestDto.RequestedItem(
                        item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequest())),
                LocalDateTime.now());
        ItemRequest expectedRequest = new ItemRequest(1, "Test", user, List.of(item), expected.getCreated());
        final PageRequest pageRequest = new MyPageRequest(0, 1000, Sort.by(
                Sort.Direction.DESC, "created"));
        Page<ItemRequest> page = new PageImpl<>(List.of(expectedRequest));

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRequestRepository.findAll(pageRequest))
                .thenReturn(page);
        when(requestMapper.toRequestDto(any()))
                .thenReturn(expected);

        List<ItemRequestDto> result = requestService.getRequests(1, pageRequest, "user");

        assertEquals(result.toString(), List.of(expected).toString());
    }

    @Test
    @DisplayName("should get request")
    @Order(3)
    void shouldGetRequest() {
        ItemRequestDto expected = new ItemRequestDto(1,
                "Test",
                List.of(new ItemRequestDto.RequestedItem(
                        item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequest())),
                LocalDateTime.now());
        ItemRequest expectedRequest = new ItemRequest(1, "Test", user, List.of(item), expected.getCreated());

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(expectedRequest));
        when(requestMapper.toRequestDto(any()))
                .thenReturn(expected);

        ItemRequestDto result = requestService.getRequest(1, 1);

        assertEquals(result.toString(), expected.toString());
    }

    @Test
    @DisplayName("should not get request")
    @Order(4)
    void shouldNotGetRequest() {

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequest(1, 1));
    }
}
