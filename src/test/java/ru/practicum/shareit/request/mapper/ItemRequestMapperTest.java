package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;

import static java.time.LocalDateTime.now;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestMapperTest {

    User user;
    User user2;
    Item item;
    Booking booking;
    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;
    @BeforeEach
    void beforeEach() {
        user = new User(1, "User1", "user@email.ru");
        user2 = new User(2, "User2", "user2@email.ru");
        item = new Item(1, "itemName", "item description", true, user2, null);
    }

    @Test
    void mapToItemRequest() throws IOException {
        var itemRequestDto = new ItemRequestDto(
                1,
                "Red carpet",
                null,
                now(),
                of()
        );
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        assertThat(itemRequest.getDescription()).isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    void mapToItemRequestDto() throws IOException {

        var itemRequestDto = new ItemRequestDto(
                1,
                "Red carpet",
                null,
                now(),
                of()
        );
        assertThat(itemRequestDto.getDescription()).isEqualTo("Red carpet");

    }

}
