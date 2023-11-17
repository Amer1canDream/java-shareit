package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private int id = 0;
    @Qualifier("InMemoryUserStorage")
    protected final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;

    }

    public UserDto save(UserDto userDto) {
        validate(userDto);
        return storage.create(userDto);
    }

    public UserDto update(UserDto userDto, Integer userId) {
        validatePatch(userDto);
        return storage.update(userDto, userId);
    }

    public List<UserDto> getUsers() {
        List<UserDto> users = storage.getUsers();
        return users;
    }

    public UserDto get(Integer userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null.");
        }
        UserDto resultUserDto = storage.findById(userId);
        if ( resultUserDto == null ) {
            String message = ("User not found");
            log.warn(message);
            throw new NotFoundException(message);
        }
        return resultUserDto;
    }

    public void delete(Integer userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null.");
        }
        storage.delete(userId);
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email cannot be empty.");
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
    }

    private void validatePatch(UserDto userDto) {
        if ((userDto.getEmail() != null) && (!userDto.getEmail().contains("@"))) {
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
        }
    }
}
