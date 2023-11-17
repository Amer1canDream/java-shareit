package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto create(UserDto user);

    UserDto update(UserDto userDto, Integer id);

    void delete(Integer id);

    List<UserDto> getUsers();

    UserDto findById(Integer id);
}
