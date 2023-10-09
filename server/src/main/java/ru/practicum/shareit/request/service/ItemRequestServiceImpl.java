package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public ItemRequestDto createRequest(Integer userId, ItemRequestIncomeDto itemRequestDto) {
        log.info("Start to create request from user {}", userId);
        User user = getUser(userId);
        return requestMapper.toRequestDto(itemRequestRepository.saveAndFlush(
                requestMapper.toRequest(itemRequestDto, user)));
    }

    @Override
    public List<ItemRequestDto> getRequests(Integer userId, PageRequest pageRequest, String state) {
        log.info("Start to create list of requests for user {}", userId);

        User user = getUser(userId);

        if (state.equals("owner")) {
            return itemRequestRepository.findAllByRequestOwner(user, Sort.by(
                    Sort.Direction.DESC, "created"))
                    .stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else {
            List<ItemRequest> list = new ArrayList<>();
            itemRequestRepository.findAll(pageRequest).stream().forEach(ir -> {
                if (!ir.getRequestOwner().getId().equals(user.getId())) {
                    list.add(ir);
                }
            });
            return list.stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ItemRequestDto getRequest(Integer userId, Integer requestId) {
        log.info("Start to getting request with id {}", requestId);

        getUser(userId);

        return requestMapper.toRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found")));
    }

    private User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
    }
}
