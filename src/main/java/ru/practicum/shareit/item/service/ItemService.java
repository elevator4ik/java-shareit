package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int id, int userId, ItemDto itemDto);

    ItemDto getItem(int id, int userId);

    List<ItemDto> getItemsOfOwner(int userId);

    List<ItemDto> searchItem(int userId, String text);

    CommentDto createComment(int userId, int itemId, CommentDto commentDto);

}
