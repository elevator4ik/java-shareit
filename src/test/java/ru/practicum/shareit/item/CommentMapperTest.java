package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Item mapper")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig({CommentMapper.class})
public class CommentMapperTest {
    @Autowired
    CommentMapper commentMapper;

    private User user = new User(1, "test@email.ru", "Test name");
    private User user1 = new User(2, "test1@email.ru", "Test1 name");
    private Item item = new Item(
            1, "TestItem", "TestDescription", Boolean.TRUE, user, null);

    @Test
    void toComment() {
        CommentDto incoming = new CommentDto(null, "text", null, null);
        Comment expected = new Comment(null, null, "text", user1, item);

        Comment result = commentMapper.toComment(incoming, user1, item);
        expected.setCreated(result.getCreated());

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void toCommentDto() {
        Comment incoming = new Comment(1, LocalDateTime.now(), "text", user1, item);
        CommentDto expected = new CommentDto(1, "text", user1.getName(), incoming.getCreated());

        CommentDto result = commentMapper.toCommentDto(incoming);

        assertEquals(expected.toString(), result.toString());
    }
}
