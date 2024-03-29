package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.model.Item;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.equalTo;
import static java.time.LocalDateTime.*;
import static org.hamcrest.Matchers.*;
import static java.util.List.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemServiceImpl itemService;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void initialize() {
        userDto = userService.save(
                new UserDto(
                        null,
                        "Joe",
                        "joe@mail.com")
        );
        itemDto = itemService.save(
                new ItemDto(null,
                        "Pen",
                        "Blue Pen",
                        true,
                        userDto.getId(),
                        null),
                null,
                userDto.getId()
        );
    }

    private CommentDto saveCommentDto(String commentText, UserDto userDto) {
        var booker = userService.save(userDto);
        bookingService.save(
                new BookingSavingDto(
                        null,
                        now().minusSeconds(2),
                        now().minusSeconds(1),
                        itemDto.getId(),
                        booker.getId(),
                        null),
                new ItemAllFieldsDto(
                        itemDto.getId(),
                        itemDto.getName(),
                        itemDto.getDescription(),
                        true,
                        userDto.getId(),
                        null,
                        null,
                        null,
                        of()),
                booker.getId()
        );
        var commentDto = new CommentDto(
                null,
                commentText,
                itemDto.getId(),
                booker.getName(),
                now()
        );
        return itemService.saveComment(
                commentDto,
                commentDto.getItemId(),
                booker.getId());
    }


    @Test
    void saveTest() {
        var item = entityManager.createQuery(
                "SELECT item " +
                        "FROM Item item",
                Item.class).getSingleResult();
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(itemDto.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getId(), notNullValue());
    }

    @Test
    void updateTest() {
        var dto = new ItemDto(
                itemDto.getId(),
                "Bear",
                "Soft toy",
                false,
                userDto.getId(),
                null
        );
        itemService.update(dto, itemDto.getOwnerId());
        var item = entityManager.createQuery(
                "SELECT item " +
                        "FROM Item item",
                Item.class).getSingleResult();
        assertThat(item.getDescription(), equalTo(dto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(dto.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(dto.getAvailable()));
        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getId(), notNullValue());
    }

    @Test
    void getTest() {
        var itemAllFieldsDto = itemService.get(itemDto.getId(), itemDto.getOwnerId());
        var item = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.id = :id " +
                                "AND item.owner.id = :ownerId",
                        Item.class)
                .setParameter("ownerId", itemDto.getOwnerId())
                .setParameter("id", itemDto.getId())
                .getSingleResult();
        assertThat(item.getDescription(), equalTo(itemAllFieldsDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(itemAllFieldsDto.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(itemAllFieldsDto.getAvailable()));
        assertThat(item.getName(), equalTo(itemAllFieldsDto.getName()));
        assertThat(item.getId(), notNullValue());
    }

    @Test
    void getAllTest() {
        itemDto = itemService.save(
                new ItemDto(
                        null,
                        "Doll",
                        "Tall doll",
                        true,
                        userDto.getId(),
                        null),
                null,
                userDto.getId()
        );
        var allItems = itemService.getAllItems(itemDto.getOwnerId(), 0, 2);
        var items = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.owner.id = :ownerId",
                        Item.class)
                .setParameter("ownerId", itemDto.getOwnerId())
                .getResultList();
        assertThat(items.get(0).getId(), equalTo(allItems.get(0).getId()));
        assertThat(items.size(), equalTo(allItems.size()));
    }

    @Test
    void searchNotAvailableItemTest() {
        itemDto = itemService.save(
                new ItemDto(
                        null,
                        "Truck",
                        "Big truck",
                        false,
                        userDto.getId(),
                        null),
                null,
                userDto.getId()
        );
        var itemDtos = itemService.search("truck", itemDto.getOwnerId(), 0, 2);
        var items = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.available = TRUE AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
                                "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))",
                        Item.class)
                .setParameter("text", "car")
                .getResultList();
        assertThat(items.size(), equalTo(itemDtos.size()));
        assertThat(items, empty());
    }

    @Test
    void searchTest() {
        itemDto = itemService.save(
                new ItemDto(
                        1,
                        "Car",
                        "Red car",
                        true,
                        userDto.getId(),
                        null),
                null,
                userDto.getId()
        );
        var itemDtos = itemService.search("car", itemDto.getOwnerId(), 0, 2);
        var items = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.available = TRUE AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
                                "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))",
                        Item.class)
                .setParameter("text", "car")
                .getResultList();
        assertThat(items.get(0).getId(), equalTo(itemDtos.get(0).getId()));
        assertThat(items.size(), equalTo(itemDtos.size()));
    }

    @Test
    void searchNotPagableTest() {
        itemDto = itemService.save(
                new ItemDto(
                        1,
                        "Car",
                        "Red car",
                        true,
                        userDto.getId(),
                        null),
                null,
                userDto.getId()
        );
        var itemDtos = itemService.search("car", itemDto.getOwnerId(), null, null);
        var items = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.available = TRUE AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
                                "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))",
                        Item.class)
                .setParameter("text", "car")
                .getResultList();
        assertThat(items.get(0).getId(), equalTo(itemDtos.get(0).getId()));
        assertThat(items.size(), equalTo(itemDtos.size()));
    }

    @Test
    void getItemsByRequestIdTest() {
        var requester = userService.save(
                new UserDto(
                        null,
                        "Abby",
                        "abby@mail.com")
        );
        var itemRequestDto = itemRequestService.save(
                new ItemRequestDto(
                        null,
                        "I need it",
                        requester.getId(),
                        now(),
                        of()),
                requester.getId()
        );
        itemService.save(
                new ItemDto(
                        null,
                        "Thing",
                        "Little thing",
                        true,
                        userDto.getId(),
                        null),
                itemRequestDto,
                userDto.getId()
        );
        var itemsByRequestId = itemService.getItemsByRequestId(itemRequestDto.getId());
        var itemsByRequest = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.request.id = :requestId",
                        Item.class)
                .setParameter("requestId", itemRequestDto.getId())
                .getResultList();
        assertThat(itemsByRequestId.size(), equalTo(itemsByRequest.size()));
        assertThat(itemsByRequestId.size(), equalTo(0));
        assertThat(itemsByRequestId, notNullValue());
    }

    @Test
    void searchEmptyResultTest() {
        itemDto = itemService.save(
                new ItemDto(
                        null,
                        "House",
                        "Big house",
                        true,
                        userDto.getId(),
                        null),
                null,
                userDto.getId()
        );
        var itemDtos = itemService.search("nothing", itemDto.getOwnerId(), 0, 2);
        var items = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.available = TRUE AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
                                "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))",
                        Item.class)
                .setParameter("text", "nothing")
                .getResultList();
        assertThat(items.size(), equalTo(itemDtos.size()));
        assertThat(items, empty());
    }

    @Test
    void saveCommentTest() {
        var commentDto = saveCommentDto(
                "Hello there",
                new UserDto(
                        15,
                        "Douglas",
                        "douglas@mail.com")
        );
        var comment = entityManager.createQuery(
                "SELECT comment " +
                        "FROM Comment comment",
                Comment.class).getSingleResult();
        assertThat(comment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
        assertThat(comment.getItem().getId(), equalTo(commentDto.getItemId()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getId(), equalTo(commentDto.getId()));
        assertThat(comment.getId(), notNullValue());
    }

    @Test
    void getItemByRequestTest() {
        List<ItemRequest> items = new ArrayList<>();
        List<ItemDto> itemRequest = itemService.getItemsByRequests(items);
        assertThat(itemRequest.size(), equalTo(0));
    }

    @Test
    void getItemsByRequestIdEmptyResultTest() {
        var requester = userService.save(
                new UserDto(
                        null,
                        "Zoe",
                        "zoe@mail.com")
        );
        var itemRequestDto = itemRequestService.save(
                new ItemRequestDto(
                        null,
                        "I need it",
                        requester.getId(),
                        now(),
                        of()),
                requester.getId()
        );
        var itemsByRequestId = itemService.getItemsByRequestId(itemRequestDto.getId());
        var itemsByRequest = entityManager.createQuery(
                        "SELECT item " +
                                "FROM Item item " +
                                "WHERE item.request.id = :requestId",
                        Item.class)
                .setParameter("requestId", itemRequestDto.getId())
                .getResultList();
        assertThat(itemsByRequestId.size(), equalTo(itemsByRequest.size()));
        assertThat(itemsByRequestId, empty());
    }

    @Test
    void getItemsByRequestsTest() {
        List<ItemRequest> itemRequest = new ArrayList<>();
        List<ItemDto> requests = itemService.getItemsByRequests(itemRequest);
        assertNotNull(requests);;
    }

    @Test
    void getAllCommentsTest() {
        var comments = itemService.getAllComments();
        assertNotNull(comments);
    }

    @Test
    void getAllItemsTest() {
        List<ItemDtoWithBooking> items = itemService.getAllItems(1,1,1);
        assertNotNull(items);
    }

    @Test
    void getNotFoundExceptionTest() {
        final Integer id = 7;
        var exception = assertThrows(NotFoundException.class,
                () -> itemService.get(id, userDto.getId()));
        assertEquals("Item with id#" + id + " does not exist", exception.getMessage());
    }

    @Test
    void validateItemTest() {
        ItemDto itemDto = new ItemDto(1, null, "test", true, 1, null);
        ItemDto itemDto1 = new ItemDto(2, "test", null, true, 1, null);
        ItemDto itemDto2 = new ItemDto(3, "test", "test", null, 1, null);


        var exception = assertThrows(ValidationException.class,
                () -> itemService.validateItem(itemDto));
        assertEquals("Name cannot be blank", exception.getMessage());

        var exception1 = assertThrows(ValidationException.class,
                () -> itemService.validateItem(itemDto1));
        assertEquals("Name cannot be blank", exception.getMessage());

        var exception2 = assertThrows(ValidationException.class,
                () -> itemService.validateItem(itemDto2));
        assertEquals("Name cannot be blank", exception.getMessage());
    }
}
