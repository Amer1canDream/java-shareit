package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    private HashMap<Integer, User> users = new HashMap<>();
    protected final UserMapper userMapper;

    public InMemoryUserStorage(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        for (User us : users.values()) {
            if (Objects.equals(us.getEmail(), user.getEmail())) {
                throw new EmailException("Email has already exists.");
            }
        }
        setIdUser(user);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Integer id) {
        userDto.setId(id);
        User storedUser = users.get(id);
        User user = UserMapper.toUser(userDto);
        for (User us : users.values()) {
            if ((Objects.equals(us.getEmail(), user.getEmail())) && (!Objects.equals(us.getId(), user.getId()))) {
                throw new EmailException("Email has already in use.");
            }
        }
        if ((user.getEmail() != null) && (user.getName() == null)) {
            storedUser.setEmail(user.getEmail());
        }
        if ((user.getEmail() == null) && (user.getName() != null)) {
            storedUser.setName(user.getName());
        }
        if ((user.getEmail() != null) && (user.getName() != null)) {
            storedUser.setEmail(user.getEmail());
            storedUser.setName(user.getName());
        }
        users.put(storedUser.getId(), storedUser);
        return UserMapper.toUserDto(storedUser);
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> usersList = new ArrayList<User>(users.values());
        return users.values()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto findById(Integer id) {
        User user = users.get(id);
        if (user != null) {
            return UserMapper.toUserDto(user);
        }
        throw new NotFoundException("User not found");
    }

    private void setIdUser(User user) {
        id++;
        user.setId(id);
    }

}
