package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemIncomeDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(int userId, ItemIncomeDto itemDto);

    ItemDto updateItem(int id, int userId, ItemIncomeDto itemDto);

    ItemDto getItem(int id, int userId);

    List<ItemDto> getItemsOfOwner(int userId);

    List<ItemDto> searchItem(int userId, String text);

    CommentDto createComment(int userId, int itemId, CommentDto commentDto);

}
