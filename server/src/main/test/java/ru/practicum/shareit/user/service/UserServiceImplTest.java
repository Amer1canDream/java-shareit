package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager entityManager;
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void initialize() {
        userDto = saveUserDto("Jack", "jack@mail.com");
    }

    private UserDto saveUserDto(String name, String email) {
        return new UserDto(null, name, email);
    }

    private void addUsers() {
        userService.save(saveUserDto("John", "john@mail.com"));
        userService.save(saveUserDto("Bobby", "bobby@mail.com"));
        userService.save(saveUserDto("Clare", "clare@mail.com"));
    }

    @Test
    void nullTest() {
        var dto = userService.save(saveUserDto("Jack", "jack@mail.com"));
        assertNotEquals(null, dto);
    }

    @Test
    void getUsersTest() {
        List<UserDto> users = userService.getUsers();
        assertThat(users.size(), equalTo(0));
    }

    @Test
    void getTest() {
        userService.save(userDto);
        var user = entityManager.createQuery(
                        "SELECT user " +
                                "FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        var userDtoFrom = userService.get(user.getId());
        assertThat(userDtoFrom.getEmail(), equalTo(user.getEmail()));
        assertThat(userDtoFrom.getName(), equalTo(user.getName()));
        assertThat(userDtoFrom.getId(), equalTo(user.getId()));
    }

    @Test
    void saveTest() {
        userService.save(userDto);
        var user = entityManager.createQuery(
                        "SELECT user " +
                                "FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getId(), notNullValue());
    }

    @Test
    void saveExceptionTest() {
        userService.save(userDto);
        UserDto userDto1 = saveUserDto("Jack", "jack@mail.com");
        Exception exception = assertThrows(ConflictException.class, () -> {
            userService.save(userDto1);;
        });
        String expectedMessage = "Email already in use";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void updateTest() {
        userService.save(userDto);
        var user = entityManager.createQuery(
                        "SELECT user " +
                                "FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        var dto = saveUserDto("Flore", "flore@mail.com");
        userService.update(dto, user.getId());
        var updatedUser = entityManager.createQuery(
                        "SELECT user " +
                                "FROM User user " +
                                "WHERE user.id = :id",
                        User.class)
                .setParameter("id", user.getId())
                .getSingleResult();
        assertThat(updatedUser.getEmail(), equalTo(dto.getEmail()));
        assertThat(updatedUser.getName(), equalTo(dto.getName()));
        assertThat(updatedUser.getId(), notNullValue());
    }

    @Test
    void updateSameEmailTest() {
        userService.save(userDto);
        var user = entityManager.createQuery(
                        "SELECT user " +
                                "FROM User user " +
                                "WHERE user.email = :email",
                        User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        var dto = saveUserDto("Jack", "jack@mail.com");
        userService.update(dto, user.getId());
        var updatedUser = entityManager.createQuery(
                        "SELECT user " +
                                "FROM User user " +
                                "WHERE user.id = :id",
                        User.class)
                .setParameter("id", user.getId())
                .getSingleResult();
        assertThat(updatedUser.getEmail(), equalTo(dto.getEmail()));
        assertThat(updatedUser.getName(), equalTo(dto.getName()));
        assertThat(updatedUser.getId(), notNullValue());
    }

    @Test
    void deleteTest() {
        addUsers();
        var usersBefore = entityManager.createQuery(
                "SELECT user " +
                        "FROM User user",
                User.class).getResultList();
        assertThat(usersBefore.size(), equalTo(3));
        userService.delete(usersBefore.get(0).getId());
        var usersAfter = entityManager.createQuery(
                "SELECT user " +
                        "FROM User user",
                User.class).getResultList();
        assertThat(usersAfter.size(), equalTo(2));
    }

    @Test
    void getAllTest() {
        addUsers();
        var users = entityManager.createQuery(
                "SELECT user " +
                        "FROM User user",
                User.class).getResultList();
        assertThat(users.size(), equalTo(3));
    }

    @Test
    void validateEmptyEmailTest() {
        UserDto userDto = new UserDto(1,"user1",null);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userService.validate(userDto);;
        });
        String expectedMessage = "Email cannot be empty.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validateWrongEmailTest() {
        UserDto userDto = new UserDto(1,"user1","test");
        Exception exception = assertThrows(ValidationException.class, () -> {
            userService.validate(userDto);;
        });
        String expectedMessage = "Incorrect email: ";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void validatePatchTest() {
        UserDto userDto = new UserDto(1,"user1","test");
        Exception exception = assertThrows(ValidationException.class, () -> {
            userService.validatePatch(userDto);;
        });
        String expectedMessage = "Incorrect email:";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}