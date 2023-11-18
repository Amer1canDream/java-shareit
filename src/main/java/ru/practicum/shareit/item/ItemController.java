package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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
    private final ItemService itemService;

    @PostMapping()
    public ItemDto save(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                        @RequestBody ItemDto itemDto) {
        log.debug("Создание вещи у пользователя с id {}", userId);
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                            @RequestBody ItemDto itemDto,
                            @PathVariable Integer itemId) {
        log.debug("Патч вещи с id {} у пользователя с id {}", itemId, userId);
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                       @PathVariable Integer itemId) {
        log.debug("Поиск вещи с id {} у пользователя с id {}", itemId, userId);
        return itemService.get(itemId);
    }

    @GetMapping()
    public List<ItemDto> getByUser(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId) {
        log.debug("Поиск всех вещей у пользователя {} id", userId);
        return itemService.getByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                                @RequestParam(required = false) String text) {
        log.debug("Поиск строки {} у пользователя {} id", text, userId);
        return itemService.search(text);
    }
}
