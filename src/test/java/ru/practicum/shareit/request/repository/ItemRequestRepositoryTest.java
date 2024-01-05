package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    ItemRequest request;
    User user1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "user1", "user1@mail.ru"));
        request = itemRequestRepository.save(
                new ItemRequest(1, "description of request", user1, LocalDateTime.now()));
    }

    @Test
    void findItemRequestByRequester_IdIsNotOrderByCreatedDescTest() {
        User user2 = userRepository.save(new User(2, "user2", "user2@mail.ru"));
        List<ItemRequest> requests = itemRequestRepository.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(user1.getId(),
                Pageable.ofSize(10)).getContent();
        assertNotNull(requests);
        assertEquals(request.getId(), 1);
        assertEquals(request.getDescription(), "description of request");
    }

    @Test
    void findItemRequestByRequesterOrderByCreatedDescTest() {
        User user2 = userRepository.save(new User(2, "user2", "user2@mail.ru"));
        List<ItemRequest> requests = itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(user1);
        assertNotNull(requests);
        assertEquals(request.getId(), 2);
        assertEquals(request.getDescription(), "description of request");
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}