package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItem(int id);

    List<Item> getItemsOfUser(int userId);

    List<Item> searchItem(String enquiry);
}
