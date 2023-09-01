package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    final UserStorage userStorage;
    final ItemStorage itemStorage;
    final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {

        log.info("Start to create item {}", itemDto.getName());

        User owner = userStorage.getUserById(userId);
        Item item = itemMapper.toItem(itemDto, owner);

        return itemMapper.toItemDto(
                Optional.of(
                        itemStorage.createItem(item))
                        .orElseThrow(() ->
                                new ErrorException("In process of adding Item error was acquired")));
    }

    @Override
    public ItemDto updateItem(int id, int userId, ItemDto itemDto) {

        log.info("Start to update item with id {}", id);

        User user = userStorage.getUserById(userId);
        Item item = itemStorage.getItem(id);
        if (user.getId().equals(item.getOwner().getId())) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }

            item.setOwner(user);//если юзер поменял свои данные с момента последнего апдейта итема

            return itemMapper.toItemDto(
                    Optional.of(
                            itemStorage.updateItem(item))
                            .orElseThrow(() ->
                                    new ErrorException("In process of updating Item error was acquired")));
        } else {
            throw new NotFoundException("Not owner try to update Item");
        }
    }

    @Override
    public ItemDto getItem(int id) {

        log.info("Start to getting item with id {}", id);

        return itemMapper.toItemDto(
                Optional.of(
                        itemStorage.getItem(id))
                        .orElseThrow(() ->
                                new ErrorException("In process of getting Item error was acquired")));
    }

    @Override
    public List<ItemDto> getItemsOfUser(int userId) {

        log.info("Start to getting items of user with id {}", userId);

        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = itemStorage.getItemsOfUser(userId);

        for (Item i : items) {
            itemsDto.add(itemMapper.toItemDto(i));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItem(int userId, String text) {

        log.info("Start to searching item which contains {}", text);

        String enquiry = text.toLowerCase();
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = itemStorage.searchItem(enquiry);

        if (!items.isEmpty()) {
            for (Item i : items) {
                itemsDto.add(itemMapper.toItemDto(i));
            }
        }
        return itemsDto;
    }
}
