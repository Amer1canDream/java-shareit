package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;

    User user;
    Booking booking;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1, "user1", "user1@email.ru"));
        item = itemRepository.save(
                new Item(1, "item1", "description1", true, user, null));
        booking = bookingRepository.save(new Booking(1,
                LocalDateTime.of(2022, 10, 15, 13, 44, 17),
                LocalDateTime.of(2022, 10, 16, 16, 22, 22),
                item, user, BookingStatus.APPROVED));
    }

    @Test
    void findAllByBookerIdTest() {
        List<Booking> bookings = bookingRepository
                .findBookingsByBookerIsOrderByStartDesc(user, PageRequest.ofSize(10)).getContent();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllByItemsOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerIsOrderByStartDesc(user);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllCurrentByItemsOwnerIdTest() {
        LocalDateTime now = LocalDateTime.of(2022, 10, 15, 20, 22, 22);
        List<Booking> bookings = bookingRepository.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now, now,
                PageRequest.ofSize(10)).getContent();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllPastByItemsOwnerIdTest() {
        LocalDateTime now = LocalDateTime.of(2022, 10, 17, 20, 22, 22);
        List<Booking> bookings = bookingRepository.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, now,
                PageRequest.ofSize(10)).getContent();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllFutureByItemsOwnerIdTest() {
        LocalDateTime now = LocalDateTime.of(2022, 10, 14, 20, 22, 22);
        List<Booking> bookings = bookingRepository.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, now,
                PageRequest.ofSize(10)).getContent();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findAllStatusByItemsOwnerIdTest() {
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(user, BookingStatus.WAITING);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());
    }

    @Test
    void findLastBookingByItemIdTest() {
        LocalDateTime now = LocalDateTime.of(2022, 10, 17, 20, 22, 22);
        List<Booking> bookings = bookingRepository.findLastBookingByItemId(item.getId(), user.getId(), now);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void findNextBookingByItemIdTest() {
        LocalDateTime now = LocalDateTime.of(2022, 10, 13, 20, 22, 22);
        List<Booking> bookings = bookingRepository.findNextBookingByItemId(item.getId(), user.getId(), now);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}
