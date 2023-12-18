package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.model.BookingTimeState.PAST;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.mapToCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.utils.Pagination.makePageRequest;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Integer userId) {
        validateItem(itemDto);
        var user = toUser(userService.get(userId));
        var item = mapToItem(itemDto);
        if (itemDto.getRequestId() != null)
            item.setRequest(ItemRequestMapper.mapToItemRequest(
                    itemRequestDto, userService.get(itemRequestDto.getRequesterId())));
        item.setOwner(user);
        var save = itemRepository.save(item);
        return mapToItemDto(save);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Integer userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null");
        var item = itemRepository.findById(itemDto.getId()).orElseThrow(
                () -> new NotFoundException("Item with id#" + itemDto.getId() + " does not exist"));
        if (!item.getOwner().getId().equals(userId))
            throw new NotFoundException("Item has another user");
        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        var save = itemRepository.save(item);
        return mapToItemDto(save);
    }


    @Override
    public ItemAllFieldsDto get(Integer id, Integer userId) {
        var item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id#" + id + " does not exist"));
        var comments = getAllComments(id);
        var lastBooking = bookingRepository.findLastBookingByItemId(item.getId(), userId, LocalDateTime.now())
                .stream()
                .findFirst()
                .orElse(null);
        var nextBooking = bookingRepository.findNextBookingByItemId(item.getId(), userId, LocalDateTime.now())
                .stream()
                .findFirst().orElse(null);;

        return mapToItemAllFieldsDto(item,
                lastBooking,
                nextBooking,
                comments);
    }

    @Override
    public List<ItemDtoWithBooking> getAllItems(Integer userId, Integer from, Integer size) {
        return itemRepository.findByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> {
                            List<Comment> comments = getCommentsByItemId(item);
                            Booking lastBooking = bookingRepository
                                    .getFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
                            Booking nextBooking = bookingRepository
                                    .getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
                            return ItemMapper.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
                        }
                )
                .collect(Collectors.toList());
    }


    @Override
    public List<ItemDto> search(String text, Integer userId, Integer from, Integer size) {
        Stream<Item> stream;
        if (text.isBlank()) return emptyList();
        var pageRequest = makePageRequest(from, size, Sort.by("id").ascending());
        if (pageRequest == null)
            stream = itemRepository.search(text).stream();
        else
            stream = itemRepository.search(text, pageRequest).stream();
        return stream
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDto saveComment(CommentDto commentDto,
                                  Integer itemId,
                                  Integer userId) {
        if (commentDto.getText() == null || commentDto.getText().isBlank())
            throw new ValidationException("Comment text cannot be blank");
        var item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id#" + itemId + " does not exist"));
        var user = toUser(userService.get(userId));
        var bookings = bookingService.getAllBookings(userId, PAST.name());
        if (bookings.isEmpty()) throw new ValidationException("User cannot make comments");
        var comment = mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now());
        var save = commentRepository.save(comment);
        return mapToCommentDto(save);
    }

    @Override
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(toList());
    }

    @Override
    public List<CommentDto> getAllComments(Integer itemId) {
        return commentRepository.findCommentByItem_IdIsOrderByCreated(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Integer requestId) {
        return itemRepository.findAllByRequest_IdIs(requestId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIn(requests)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    private void validateItem(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Name cannot be blank");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new ValidationException("Description cannot be blank");
        if (item.getAvailable() == null)
            throw new ValidationException("Available cannot be null");
    }

    public List<Comment> getCommentsByItemId(Item item) {
        return commentRepository.getByItemIdOrderByCreatedDesc(item.getId());
    }

}
