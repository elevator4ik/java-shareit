package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemStorageImpl implements ItemStorage {

    private Map<Integer, Item> itemStorage = new HashMap<>();
    private int id = 1;

    @Override
    public Item createItem(Item item) {

        item.setId(id);
        id++;
        itemStorage.put(item.getId(), item);
        return getItem(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        itemStorage.replace(item.getId(), item);
        return getItem(item.getId());
    }

    @Override
    public Item getItem(int id) {
        Item item = itemStorage.get(id);
        if (item != null) {
            return item;
        } else {
            throw new NotFoundException("Item with id " + id + " not found");
        }
    }

    @Override
    public List<Item> getItemsOfUser(int userId) {
        List<Item> list = new ArrayList<>();
        for (Item i : itemStorage.values()) {
            if (i.getOwner().getId() == userId) {
                list.add(i);
            }
        }
        return list;
    }

    @Override
    public List<Item> searchItem(String enquiry) {

        List<Item> items = new ArrayList<>();
        for (Item i : itemStorage.values()) {
            if (i.getAvailable()) {
                if (i.getName().toLowerCase().contains(enquiry) || i.getDescription().toLowerCase().contains(enquiry)) {
                    items.add(i);
                }
            }
        }
        return items;
    }
}
