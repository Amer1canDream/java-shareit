package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.user.mapper.UserMapper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static java.util.Optional.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    private UserService userService;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void initialize() {
        userService = new UserService(userRepository, userMapper);
        userDto = UserDto.builder()
                .id(1)
                .name("Paul")
                .email("paul@mail.com")
                .build();
        user = toUser(userDto);
    }

    @Test
    void saveTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        var save = userService.save(userDto);
        assertEquals(save.getEmail(), user.getEmail());
        assertEquals(save.getName(), user.getName());
        assertEquals(save.getId(), user.getId());
    }

    @Test
    void saveUserSameEmailTest() {
        when(userRepository.save(any()))
                .thenThrow(EmailException.class);
        assertThrows(EmailException.class,
                () -> userService.save(userDto));

    }

    @Test
    void updateUserNameTest() {
        var userDto1 = new UserDto(1, "Daniel", null);
        var userDto2 = new UserDto(1, "Daniel", userDto.getEmail());
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.save(userDto);
        when(userRepository.save(any()))
                .thenReturn(toUser(userDto2));
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(toUser(userDto2)));
        var dto = userService.update(userDto1, userDto2.getId());
        assertNotEquals(dto.getEmail(), userDto1.getEmail());
        assertEquals(dto.getName(), userDto2.getName());
        assertEquals(dto.getId(), userDto2.getId());
    }

    @Test
    void updateTest() {
        var updatedUser = new UserDto(
                1,
                "Nagel",
                "nagel@mail.com");
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.save(userDto);
        when(userRepository.save(any()))
                .thenReturn(toUser(updatedUser));
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(toUser(updatedUser)));
        var dto = userService.update(updatedUser, updatedUser.getId());
        assertEquals(dto.getEmail(), updatedUser.getEmail());
        assertEquals(dto.getName(), updatedUser.getName());
        assertEquals(dto.getId(), updatedUser.getId());
    }

    @Test
    void getUserUserNotFoundTest() {
        when(userRepository.findById(any()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> userService.get(7));
    }

    @Test
    void updateUserEmailTest() {
        var userDto1 = new UserDto(1, null, "john@mail.com");
        var userDto2 = new UserDto(1, userDto.getName(), "john@mail.com");
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.save(userDto);
        when(userRepository.save(any()))
                .thenReturn(toUser(userDto2));
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(toUser(userDto2)));
        var dto = userService.update(userDto1, userDto2.getId());
        assertNotEquals(dto.getName(), userDto1.getName());
        assertEquals(dto.getEmail(), userDto2.getEmail());
        assertEquals(dto.getId(), userDto2.getId());
    }

    @Test
    void updateUserSameEmailTest() {
        var dto = new UserDto(2, "Paul", "paul@mail.com");
        when(userRepository.findById(any()))
                .thenReturn(ofNullable(toUser(dto)));
        when(userRepository.save(any()))
                .thenThrow(EmailException.class);
        assertThrows(EmailException.class,
                () -> userService.update(
                        userDto,
                        1)
        );
    }

    @Test
    void deleteTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        var dto = userService.save(userDto);
        userService.delete(dto.getId());
        verify(userRepository, times(1))
                .deleteById(user.getId());
    }
}