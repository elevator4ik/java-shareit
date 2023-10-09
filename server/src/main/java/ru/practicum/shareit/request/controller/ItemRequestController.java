package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestBody ItemRequestIncomeDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getRequests(userId, null, "owner");
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam("from") Integer from,
                                               @RequestParam("size") Integer size) {
        final PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(
                Sort.Direction.DESC, "created"));
        return itemRequestService.getRequests(userId, pageRequest, "user");
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable Integer requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }
}