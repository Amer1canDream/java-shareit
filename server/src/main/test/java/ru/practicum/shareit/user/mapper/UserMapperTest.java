package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    User user;
    User user2;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "User1", "user@email.ru");
        user2 = new User(2, "User2", "user2@email.ru");
    }

    @Test
    void toUserDtoTest() {
        UserDto userDto = UserMapper.toUserDto(user);
        assertEquals(userDto.getId(), user.getId());
    }

    @Test
    void toUserTest() {
        UserDto userDto = UserMapper.toUserDto(user);
        User user3 = UserMapper.toUser(userDto);
        assertEquals(userDto.getId(), user3.getId());
    }
}

