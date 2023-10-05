package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import ru.practicum.shareit.item.dto.ItemDto;

@JsonTest
@DisplayName("Item dto json")
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;
    private final ItemDto dto = new ItemDto(1, "Saw", "Tool", Boolean.TRUE,
            null, null, null, new ArrayList<>());
    private final String dtoJson = "{\n" +
            "    \"id\":1," +
            "    \"name\":\"Saw\"," +
            "    \"description\":\"Tool\"," +
            "    \"available\":true," +
            "    \"requestId\":null," +
            "    \"nextBooking\":null," +
            "    \"lastBooking\":null," +
            "    \"comments\":[]" +
            "}";

    @Test
    @DisplayName("should serialize")
    void testSerialize() throws Exception {

        var json = jacksonTester.write(dto);

        assertThat(json).isEqualToJson(dtoJson);
    }

    @Test
    @DisplayName("should deserialize")
    void testDeserialize() throws IOException {

        var item = new ItemDto(1, "Saw", "Tool", Boolean.TRUE,
                null, null, null, new ArrayList<>());

        var dto = jacksonTester.parseObject(dtoJson);

        assertThat(dto).extracting("id").isEqualTo(item.getId());
        assertThat(dto).extracting("name").isEqualTo(item.getName());
        assertThat(dto).extracting("description").isEqualTo(item.getDescription());
        assertThat(dto).extracting("available").isEqualTo(item.getAvailable());
        assertThat(dto).extracting("requestId").isEqualTo(item.getRequestId());
        assertThat(dto).extracting("nextBooking").isEqualTo(item.getNextBooking());
        assertThat(dto).extracting("lastBooking").isEqualTo(item.getLastBooking());
        assertThat(dto).extracting("comments").isEqualTo(item.getComments());
    }
}