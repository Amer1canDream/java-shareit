package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto save(UserDto userDto) {
        validate(userDto);
        User user = UserMapper.toUser(userDto);
        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already in use");
        }
        log.info("{} is saved", savedUser);

        return UserMapper.toUserDto(savedUser);
    }

    @Transactional
    public UserDto update(UserDto userDto, Integer userId) {
        validatePatch(userDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " is not found"));

        UserMapper.toUser(userDto);
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());

        try {
            log.info("{} is updated", user);
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email already in use");
        }
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    public UserDto get(Integer userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID #" + userId + " does not exist."));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public void delete(Integer userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        userRepository.deleteById(userId);
    }

    void validate(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email cannot be empty.");
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@"))
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
    }

    void validatePatch(UserDto userDto) {
        if ((userDto.getEmail() != null) && (!userDto.getEmail().contains("@"))) {
            throw new ValidationException("Incorrect email: " + userDto.getEmail() + ".");
        }
    }
}
