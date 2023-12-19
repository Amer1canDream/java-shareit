package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;


class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper();

    private UserMapper userMapper;

    @Test
    void mapToItemTest() {
        User user1 = new User(null,"user1", "user1@emmail.ru");
        UserDto user1Dto = UserMapper.toUserDto(user1);
        user1.setId(1);
        Item item1 = new Item(1,"item1","description1",true,user1,null);
        ItemDto item1Dto = mapToItemDto(item1);
        Item item1after = mapToItem(item1Dto);
        assertThat(item1.getId().equals(item1after.getId()));
    }

    @Test
    void mapToItemDtoTest() {
        User user1 = new User(null,"user1","user1@emmail.ru");
        UserDto user1Dto = UserMapper.toUserDto(user1);
        user1.setId(1);
        Item item1 = new Item(1,"item1","description1",true, user1, null);
        ItemDto item1Dto = mapToItemDto(item1);
        assertThat(item1.getId().equals(item1Dto.getId()));
    }

    @Test
    void mapToItemAllFieldsDtoTest() {
        User user1 = new User(null,"user1","user1@emmail.ru");
        UserDto user1Dto = UserMapper.toUserDto(user1);
        user1.setId(1);
        Item item1 = new Item(1, "item1", "description1", true, user1, null);
        ItemAllFieldsDto itemDto = mapToItemAllFieldsDto(item1, null, null, null);
        assertThat(item1.getId().equals(itemDto.getId()));
    }

    @Test
    void toItemDtoWithBookingTest() {
        User user1 = new User(null,"user1","user1@emmail.ru");
        UserDto user1Dto = UserMapper.toUserDto(user1);
        user1.setId(1);

        Item item1 = new Item(1, "item1", "description1", true, user1, null);

        Comment comment1 = new Comment(1, "text", item1, user1, LocalDateTime.now());

        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);

        ItemDtoWithBooking itemDtoWithBooking = toItemDtoWithBooking(comments, null, null, item1);
        assertThat(item1.getId().equals(itemDtoWithBooking.getId()));
    }
}
