package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    void mapToItemRequestTest() throws IOException {
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
    void mapToItemRequestDtoTest() throws IOException {

        var itemRequestDto = new ItemRequestDto(
                1,
                "Red carpet",
                null,
                now(),
                of()
        );

        var userDto = new UserDto(5,"test","test@test.ru");
        var itemDto = new ItemDto(7, "test", "test", true, 5, null);
        var itemRequest = new ItemRequest(1, "test", user, now());
        var itemRequest1 = ItemRequestMapper.mapToItemRequest(itemRequestDto, userDto);
        List<ItemDto> items = new ArrayList<>();
        items.add(itemDto);
        var itemRequestDto1 = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        var itemRequestDto2 = ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Red carpet");
        assertThat(itemRequest.getDescription().equals(itemRequestDto1.getDescription()));
        assertThat(itemRequestDto2.getDescription().equals(itemRequest.getDescription()));
        assertThat(itemRequest1.getDescription().equals(itemRequestDto.getDescription()));

    }

}
