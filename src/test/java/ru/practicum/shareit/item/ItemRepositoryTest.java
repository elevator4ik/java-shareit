package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Item Repository")
@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setId(2);
        user.setName("user");
        user.setEmail("user@email");
        user = userRepository.save(user);
        item1 = new Item();
        item1.setId(2);
        item1.setOwner(user);
        item1.setName("item 1");
        item1.setDescription("item 1 description");
        item1.setAvailable(true);
        item1 = itemRepository.save(item1);
        item2 = new Item();
        item2.setId(3);
        item2.setOwner(user);
        item2.setName("item 2");
        item2.setDescription("item 2 description");
        item2.setAvailable(true);
        item2 = itemRepository.save(item2);
    }

    @Test
    @DisplayName("Items search")
    void shouldFindAllItemsByUser() {
        String text = "item";

        final List<Item> result = itemRepository.search(text);
        assertEquals(2, result.size());

    }
}