package ru.practicum.shareit.item.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    private final Item item2 = new Item();
    private final Comment comment1 = new Comment();
    private final Comment comment2 = new Comment();

    @BeforeEach
    public void addComments() {
        User user = new User();
        user.setName("name");
        user.setEmail("mail@mail.ru");

        Item item1 = new Item();
        item1.setName("1");
        item1.setDescription("1");
        item1.setAvailable(true);
        item1.setOwner(user);
        item2.setName("2");
        item2.setDescription("2");
        item2.setAvailable(false);
        item2.setOwner(user);

        comment1.setText("1");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());

        comment2.setText("2");
        comment2.setItem(item2);
        comment2.setAuthor(user);
        comment2.setCreated(LocalDateTime.now());

        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @AfterEach
    public void deleteComments() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemId() {
        List<Comment> actualComments = commentRepository.findById(item2.getId()).stream().collect(Collectors.toList());

        assertThat(1, equalTo(actualComments.size()));
        assertThat(comment2, equalTo(actualComments.get(0)));
    }

}