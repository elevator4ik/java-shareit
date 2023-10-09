package ru.practicum.shareit.request.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {

    public ItemRequest toRequest(ItemRequestIncomeDto itemRequestDto, User requestOwner) {
        return new ItemRequest(null,
                itemRequestDto.getDescription(),
                requestOwner,
                new ArrayList<>(),
                LocalDateTime.now());
    }

    public ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                toRequestedItem(itemRequest),
                itemRequest.getCreated()
        );
    }

    private List<ItemRequestDto.RequestedItem> toRequestedItem(ItemRequest itemRequest) {
        List<Item> list = itemRequest.getRequestedItems();
        List<ItemRequestDto.RequestedItem> newList = new ArrayList<>();

        if (!list.isEmpty()) {
            for (Item i : list) {
                newList.add(new ItemRequestDto.RequestedItem(i.getId(),
                        i.getName(),
                        i.getDescription(),
                        i.getAvailable(),
                        i.getRequest()));
            }
        }
        return newList;
    }
}
