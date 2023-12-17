package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exceptions.NotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItem;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.when;
import static java.util.List.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    private ItemService itemService;
    private ItemDto itemDto;
    private UserDto userDto;
    private Item item;

    @BeforeEach
    void initialize() {
        itemService = new ItemServiceImpl(
                    itemRepository,
                    commentRepository,
                    userService,
                    bookingService,
                    userRepository,
                    bookingRepository
        );
        userDto = new UserDto(
                1,
                "Eddie",
                "eddie@mail.com");
        item = new Item(
                1,
                "Pocket",
                "Deep pocket",
                true,
                toUser(userDto),
                null);
        itemDto = mapToItemDto(item);
    }

    private ItemDto saveItemDto() {
        when(userService.get(any()))
                .thenReturn(userDto);
        when(itemRepository.save(any()))
                .thenReturn(mapToItem(itemDto));
        return itemService.save(itemDto, null, userDto.getId());
    }

    @Test
    void saveTest() {
        var saved = saveItemDto();
        assertEquals(saved.getName(), item.getName());
        assertEquals(saved.getId(), item.getId());
    }

    @Test
    void updateTest() {
        var dto = saveItemDto();
        var updated = new Item(
                dto.getId(),
                "Anthony",
                itemDto.getDescription(),
                itemDto.getAvailable(),
                toUser(userDto),
                null
        );
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(updated);
        var update = itemService.update(mapToItemDto(updated), userDto.getId());
        assertNotEquals(dto.getName(), update.getName());
        assertEquals(dto.getId(), update.getId());
    }

    @Test
    void searchTest() {
        saveItemDto();
        when(itemRepository.search(anyString()))
                .thenReturn(of(item));
        var search = itemService.search(
                "oops",
                userDto.getId(),
                null,
                null
        );
        assertEquals(search.get(0).getId(), item.getId());
        assertEquals(search.size(), 1);
    }

    @Test
    void getItemNotFoundTest() {
        saveItemDto();
        when(itemRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> itemService.get(42, userDto.getId()));
    }

    @Test
    void saveCommentNotFoundItemTest() {
        var commentDto = new CommentDto(
                1,
                "pink rose",
                itemDto.getId(),
                userDto.getName(),
                now()
        );
        when(itemRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> itemService.saveComment(commentDto, 42, 2));
    }

    @Test
    void searchEmptyTextTest() {
        var search = itemService.search(
                "",
                userDto.getId(),
                null,
                null
        );
        assertEquals(search.size(), 0);
    }

    @Test
    void searchEmptyResultTest() {
        saveItemDto();
        when(itemRepository.search(anyString()))
                .thenReturn(of());
        var search = itemService.search(
                "Golden hand",
                userDto.getId(),
                null,
                null
        );
        assertEquals(search.size(), 0);
    }

    @Test
    void getCommentsTest() {
        var commentDto = new CommentDto(
                1,
                "My comment",
                itemDto.getId(),
                userDto.getName(),
                now()
        );
        var comment = new Comment(
                1,
                commentDto.getText(),
                item,
                toUser(userDto),
                now()
        );
        when(commentRepository.findCommentByItem_IdIsOrderByCreated(anyInt()))
                .thenReturn(of(comment));
        var allComments = itemService.getAllComments(item.getId());
        assertEquals(allComments.get(0).getId(), comment.getId());
        assertEquals(allComments.size(), 1);
    }

    @Test
    void getAllCommentsTest() {
        var commentDto = new CommentDto(
                1,
                "space",
                itemDto.getId(),
                userDto.getName(),
                now()
        );
        var comment = new Comment(
                1,
                commentDto.getText(),
                item,
                toUser(userDto),
                now()
        );
        when(commentRepository.findAll())
                .thenReturn(of(comment));
        var allComments = itemService.getAllComments();
        assertEquals(allComments.get(0).getId(), comment.getId());
        assertEquals(allComments.size(), 1);
    }
}