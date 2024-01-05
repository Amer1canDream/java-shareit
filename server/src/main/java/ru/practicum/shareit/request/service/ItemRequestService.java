package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Integer userId);

    ItemRequestDto save(ItemRequestDto itemRequestDto, Integer requesterId);

    ItemRequestDto getItemRequestById(Integer requestId, Integer userId);

    List<ItemRequestDto> getAllItemRequests(Integer userId);
}