package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemIncomeDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemIncomeDto itemDto, Integer userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Integer itemId, Integer userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateItem(ItemIncomeDto itemDto, Integer itemId, Integer userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Integer userId, Integer itemId) {
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getItems(Integer userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> searchItem(String text, Integer userId) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", userId, parameters);
    }
}