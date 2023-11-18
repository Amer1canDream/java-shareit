package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    private int id = 0;
    private HashMap<Integer, Item> items = new HashMap<>();
    protected final ItemMapper itemMapper;

    public InMemoryItemStorage(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public Item get(Integer item) {
        return items.get(item);
    }

    @Override
    public Item save(Item item) {
        setIdItem(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, Integer itemId) {
        Item storedItem = items.get(itemId);
        if (!Objects.equals(item.getOwner().getId(), storedItem.getOwner().getId())) {
            throw new NotFoundException("Not user item");
        }
        if (item.getAvailable() != null) {
            storedItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            storedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            storedItem.setDescription(item.getDescription());
        }
        items.put(storedItem.getId(), storedItem);
        return items.get(storedItem.getId());
    }

    @Override
    public List<Item> findByUser(Integer userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item: items.values()) {
            if (Objects.equals(item.getOwner().getId(), userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> search(String searchedString) {
        List<Item> searchedItems = new ArrayList<>();
        if (searchedString.isBlank()) {
            return searchedItems;
        }
        for (Item item: items.values()) {
            if (item.getName().toLowerCase().contains(searchedString.toLowerCase()) && item.getAvailable()) {
                searchedItems.add(item);
            } else if (item.getDescription().toLowerCase().contains(searchedString.toLowerCase()) && item.getAvailable()) {
                searchedItems.add(item);
            }
        }
        return searchedItems;
    }

    private void setIdItem(Item item) {
        id++;
        item.setId(id);
    }
}
