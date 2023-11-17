package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto save(ItemDto itemDto, Integer userId) {
        validateItem(itemDto);
        User user = toUser(userService.get(userId));
        Item item = mapToItem(itemDto);
        item.setOwner(user);
        Item save = itemStorage.save(item);
        return mapToItemDto(save);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Integer userId, Integer itemId) {
        User user = toUser(userService.get(userId));
        Item item = mapToItem(itemDto);
        item.setOwner(user);
        Item save = itemStorage.update(item, itemId);
        return mapToItemDto(save);
    }

    @Override
    public ItemDto get(Integer itemId) {
        return mapToItemDto(itemStorage.get(itemId));
    }

    @Override
    public List<ItemDto> getByUser(Integer userId) {
        return itemStorage.findByUser(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> search(String searchedString) {
        return itemStorage.search(searchedString)
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
}
