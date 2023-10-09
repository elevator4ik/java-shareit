package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Integer userId, ItemRequestIncomeDto itemRequestDto);

    List<ItemRequestDto> getRequests(Integer userId, PageRequest pageRequest, String state);

    ItemRequestDto getRequest(Integer userId, Integer requestId);
}
