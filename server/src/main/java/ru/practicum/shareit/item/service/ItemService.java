package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Integer userId);

    ItemDto update(ItemDto itemDto, Integer userId);

    CommentDto saveComment(CommentDto commentDto, Integer itemId, Integer userId);

    List<CommentDto> getAllComments(Integer itemId);

    List<CommentDto> getAllComments();

    List<ItemDto> getItemsByRequestId(Integer requestId);

    List<ItemDto> getItemsByRequests(List<ItemRequest> requests);

    ItemAllFieldsDto get(Integer id, Integer itemId);

    List<ItemDtoWithBooking> getAllItems(Integer userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer userId, Integer from, Integer size);

}
