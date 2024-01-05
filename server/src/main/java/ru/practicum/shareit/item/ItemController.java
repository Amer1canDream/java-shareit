package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping()
    public ItemDto save(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                        @RequestBody ItemDto itemDto) {
        var itemRequestDto = itemDto.getRequestId() != null
                ? itemRequestService.getItemRequestById(itemDto.getRequestId(), userId)
                : null;
        log.debug("Создание вещи у пользователя с id {}", userId);
        log.debug("Создание вещи у пользователя с id {}", userId);
        return itemService.save(itemDto, itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                            @RequestBody ItemDto itemDto,
                            @PathVariable Integer itemId) {
        log.debug("Патч вещи с id {} у пользователя с id {}", itemId, userId);
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                       @PathVariable Integer itemId) {
        log.debug("Поиск вещи с id {} у пользователя с id {}", itemId, userId);
        return itemService.get(itemId, userId);
    }

    @GetMapping()
    public List<ItemDtoWithBooking> getAllItems(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                                @RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size,
                                @RequestParam(required = false) String text) {
        return itemService.search(text, userId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                                  @RequestBody CommentDto commentDto,
                                  @PathVariable Integer itemId) {
        return itemService.saveComment(commentDto, itemId, userId);
    }
}
