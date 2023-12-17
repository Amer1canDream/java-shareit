package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    protected User user;
    protected UserDto userDtoIn;
    protected UserDto userDtoOut;

    @BeforeEach
    protected void setUp() {
        user = User.builder()
                .id(1)
                .name("user")
                .email("user@gmail.com")
                .build();

        userDtoIn = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        userDtoOut = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void load() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void existsById_shouldInvokeRepositoryAndReturnTheSame() {
        User savedUser = userRepository.save(user);
        assertThat(userRepository.existsById(savedUser.getId())).isTrue();
    }
}