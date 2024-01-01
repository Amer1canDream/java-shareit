package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.mapper.BookingMapper.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static ru.practicum.shareit.booking.model.BookingTimeState.*;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItem;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.utils.Pagination.makePageRequest;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingAllFieldsDto save(BookingSavingDto bookingSavingDto, ItemAllFieldsDto itemDto, Integer bookerId) {
        if (itemDto.getOwnerId().equals(bookerId))
            throw new NotFoundException("Item with id#" + itemDto.getId() + " cannot be booked by his owner");
        if (!itemDto.getAvailable())
            throw new ValidationException("Item with id#" + itemDto.getId() + " cannot be booked");
        validate(bookingSavingDto);
        var booker = toUser(userService.get(bookerId));
        var item = mapToItem(itemDto);

        var booking = mapToBooking(bookingSavingDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        var savedBooking = bookingRepository.save(booking);
        return mapToBookingAllFieldsDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingAllFieldsDto approve(Integer bookingId, boolean approved, Integer userId) {
        var booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id#" + bookingId + " does not exist"));
        if (booking.getBooker().getId().equals(userId))
            throw new NotFoundException("There is no available approve for the user with id#" + userId);
        if (!booking.getItem().getOwner().getId().equals(userId)
                || !booking.getStatus().equals(BookingStatus.WAITING))
            throw new ValidationException("Booking state cannot be updated");
        booking.setStatus(approved ? APPROVED : BookingStatus.REJECTED);
        var savedBooking = bookingRepository.save(booking);
        return mapToBookingAllFieldsDto(savedBooking);
    }

    @Override
    public BookingAllFieldsDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id#" + bookingId + " does not exist"));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("There is no available approve for the user with id#" + userId);
        }
        return mapToBookingAllFieldsDto(booking);
    }

    @Override
    public List<BookingAllFieldsDto> getAllBookings(Integer bookerId, String state) {
        Stream<Booking> stream = null;
        var userDto = userService.get(bookerId);
        var user = toUser(userDto);
        if (state == null || ALL.name().equals(state))
            stream = bookingRepository.findBookingsByBookerIsOrderByStartDesc(user)
                    .stream();
        if (PAST.name().equals(state))
            stream = bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, now())
                    .stream();
        if (CURRENT.name().equals(state))
            stream = bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now(), now())
                    .stream();
        if (FUTURE.name().equals(state))
            stream = bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, now())
                    .stream();
        if (Arrays.stream(BookingStatus.values()).anyMatch(bookingStatus -> bookingStatus.name().equals(state)))
            stream = bookingRepository
                    .findBookingsByBookerIsAndStatusIsOrderByStartDesc(user, BookingStatus.valueOf(state))
                    .stream();
        if (stream != null)
            return stream
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        else
            throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public List<BookingAllFieldsDto> getAllBookings(Integer bookerId, String state, Integer from, Integer size) {
        Stream<Booking> stream = null;
        Pageable pageRequest = makePageRequest(from, size, Sort.by("start").descending());
        var userDto = userService.get(bookerId);
        User user = toUser(userDto);
        if (state == null || ALL.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByBookerIsOrderByStartDesc(user)
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByBookerIsOrderByStartDesc(user, pageRequest)
                        .stream();
        }
        if (PAST.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, now())
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, now(), pageRequest)
                        .stream();
        }
        if (CURRENT.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now(), now())
                        .stream();
            else
                stream = bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now(), now(), pageRequest)
                        .stream();
        }
        if (FUTURE.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, now())
                        .stream();
            else
                stream = bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, now(), pageRequest)
                        .stream();
        }
        if (Arrays.stream(BookingStatus.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            if (pageRequest == null)
                stream = bookingRepository.findBookingsByBookerIsAndStatusIsOrderByStartDesc(user, BookingStatus.valueOf(state))
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByBookerIsAndStatusIsOrderByStartDesc(user, BookingStatus.valueOf(state), pageRequest)
                        .stream();
        }
        if (stream != null)
            return stream
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        else
            throw new ValidationException("Unknown state: " + state);
    }

    @Override
    public List<BookingAllFieldsDto> getBookingsByOwnerId(Integer userId, String state, Integer from, Integer size) {
        Stream<Booking> stream = null;
        var pageRequest = makePageRequest(from, size, Sort.by("start").descending());
        var user = toUser(userService.get(userId));
        if (state == null || state.equals(ALL.name())) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByItemOwnerIsOrderByStartDesc(user)
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByItemOwnerIsOrderByStartDesc(user, pageRequest)
                        .stream();
        }
        if (PAST.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user, now())
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user, now(), pageRequest)
                        .stream();
        }
        if (CURRENT.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now(), now())
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now(), now(), pageRequest)
                        .stream();
        }
        if (FUTURE.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByItemOwnerAndStartAfterOrderByStartDesc(user, now())
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByItemOwnerAndStartAfterOrderByStartDesc(user, now(), pageRequest)
                        .stream();
        }
        if (Arrays.stream(BookingStatus.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            if (pageRequest == null)
                stream = bookingRepository
                        .findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(user, BookingStatus.valueOf(state))
                        .stream();
            else
                stream = bookingRepository
                        .findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(user, BookingStatus.valueOf(state), pageRequest)
                        .stream();
        }
        if (stream != null)
            return stream
                    .map(BookingMapper::mapToBookingAllFieldsDto)
                    .collect(toList());
        else throw new ValidationException("Unknown state: " + state);
    }

    private void validate(BookingSavingDto bookingSavingDto) {
        if (bookingSavingDto.getStart() == null)
            throw new ValidationException("Please enter your start booking date");
        if (bookingSavingDto.getEnd() == null)
            throw new ValidationException("Please enter your end booking date");
        if (bookingSavingDto.getStart().toLocalDate().isBefore(LocalDate.now()))
            throw new ValidationException("Incorrect start booking date");
        if (bookingSavingDto.getEnd().isBefore(bookingSavingDto.getStart())
                || bookingSavingDto.getEnd().toLocalDate().isBefore(LocalDate.now()))
            throw new ValidationException("Incorrect end booking date");
        if (bookingSavingDto.getStart().equals(bookingSavingDto.getEnd()))
            throw new ValidationException("Start and end date the same");
    }
}
