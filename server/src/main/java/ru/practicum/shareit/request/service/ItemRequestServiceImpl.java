package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.mapToItemRequest;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.mapToItemRequestDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.utils.Pagination.makePageRequest;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Integer userId) {
        List<ItemRequest> requests;
        var pageRequest = makePageRequest(from, size, Sort.by("created").descending());
        if (pageRequest == null) {
            requests = itemRequestRepository.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId);
        } else {
            requests = itemRequestRepository.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId, pageRequest)
                    .stream()
                    .collect(toList());

        }
        var items = itemService.getItemsByRequests(requests)
                .stream()
                .collect(groupingBy(ItemDto::getRequestId));
        return requests
                .stream()
                .map(itemRequest -> mapToItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(toList());
    }

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Integer requesterId) {
        validate(itemRequestDto);
        var userDto = userService.get(requesterId);
        var user = toUser(userDto);
        var itemRequest = mapToItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(now());
        var save = itemRequestRepository.save(itemRequest);
        return mapToItemRequestDto(save);
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer requestId, Integer userId) {
        toUser(userService.get(userId));
        var items = itemService.getItemsByRequestId(requestId);
        var itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request with ID#" + requestId + " does not exist"));
        return mapToItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer userId) {
        var userDto = userService.get(userId);
        var user = toUser(userDto);
        var itemRequests = itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(user);
        var items = itemService.getItemsByRequests(itemRequests)
                .stream()
                .collect(groupingBy(ItemDto::getRequestId));
        return itemRequests.stream()
                .map(itemRequest -> mapToItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(toList());
    }


    private void validate(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Request cannot be null or blank");
        }
    }
}