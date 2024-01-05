package ru.practicum.shareit.error;

public class BookingStateException extends IllegalArgumentException {
    public BookingStateException(String message) {
        super(message);
    }
}
