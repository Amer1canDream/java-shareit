package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.BookingStateException;


import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
@Validated
@Slf4j
public class BookingController {
    private static final String HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping()
    @Validated
    public ResponseEntity<Object> save(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                                       @RequestBody BookingSavingDto bookingSavingDto) {
        if (bookingSavingDto.getStart().isAfter(bookingSavingDto.getEnd()))
            throw new IllegalArgumentException("Incorrect date of booking");
        return bookingClient.save(bookingSavingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(value = HEADER_SHARER_USER_ID, required = false) Integer userId,
                                       @RequestParam(required = false) boolean approved,
                                       @PathVariable Integer bookingId) {
        log.info("{} bookingid {} userid - approve request", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @RequestHeader(HEADER_SHARER_USER_ID) Integer userId) {
        var state = BookingState.from(stateParam).orElseThrow(
                () -> new BookingStateException("Unknown state: " + stateParam));
        return bookingClient.getByOwner(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @RequestHeader(HEADER_SHARER_USER_ID) Integer userId) {
        var state = BookingState.from(stateParam).orElseThrow(
                () -> new BookingStateException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HEADER_SHARER_USER_ID) Integer userId,
                                             @PathVariable Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }
}
