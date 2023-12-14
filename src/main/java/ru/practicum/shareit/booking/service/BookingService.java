package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;

import java.util.List;

public interface BookingService {

    List<BookingAllFieldsDto> getBookingsByOwnerId(Integer userId, String state, Integer from, Integer size);

    List<BookingAllFieldsDto> getAllBookings(Integer bookerId, String state, Integer from, Integer size);

    BookingAllFieldsDto save(BookingSavingDto booking, ItemAllFieldsDto itemDto, Integer bookerId);

    BookingAllFieldsDto approve(Integer bookingId, boolean approved, Integer userId);

    List<BookingAllFieldsDto> getAllBookings(Integer bookerId, String state);

    BookingAllFieldsDto getBookingById(Integer bookingId, Integer userId);

}
