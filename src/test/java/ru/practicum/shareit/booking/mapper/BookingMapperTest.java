package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    User user;
    User user2;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "User1", "user@email.ru");
        user2 = new User(2, "User2", "user2@email.ru");
        item = new Item(1, "itemName", "item description", true, user2, null);
        booking = new Booking(1, LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22), item, user,
                BookingStatus.WAITING);
    }

    @Test
    void toBookingItemDto() {
        BookingAllFieldsDto bookingDto = BookingMapper.mapToBookingAllFieldsDto(booking);
        assertNotNull(bookingDto);
        assertEquals(BookingAllFieldsDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
    }

    @Test
    void toBooking() {
        BookingSavingDto bookingDto = new BookingSavingDto(item.getId(),
                LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22),
                item.getId(),
                user.getId(),
                "ALL");
        Booking newBooking = BookingMapper.mapToBooking(bookingDto);
        assertNotNull(newBooking);
    }
}