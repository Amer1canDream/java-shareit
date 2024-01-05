package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.request.client.RequestClient;
import org.springframework.validation.annotation.Validated;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Positive;
import javax.validation.Valid;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestHeader(required = false, value = HEADER_SHARER_USER_ID) Integer userId,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestClient.getAllItemRequests(from, size, userId);
    }

    @Validated
    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = HEADER_SHARER_USER_ID) Integer userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return requestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = HEADER_SHARER_USER_ID) Integer userId) {
        return requestClient.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(value = HEADER_SHARER_USER_ID) Integer userId,
                                                 @PathVariable Integer requestId) {
        return requestClient.getItemRequest(requestId, userId);
    }
}