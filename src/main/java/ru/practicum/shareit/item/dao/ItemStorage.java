package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item save(Item item);
    Item update(Item item, Integer itemId);
    Item get(Integer itemId);
    List<Item> findByUser(Integer userId);
    List<Item> search(String searchedString);
}
