package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAllFieldsDto;
import ru.practicum.shareit.booking.dto.BookingSavingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static java.time.LocalDateTime.now;
import static java.util.List.of;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private BookingAllFieldsDto bookingAllFieldsDto;
    private final BookingService bookingService;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private ItemDto itemDto;
    private UserDto owner;

    @BeforeEach
    void initialize() {
        owner = userService.save(
                new UserDto(
                        1,
                        "Lora",
                        "lora@mail.com")
        );
        var booker = userService.save(
                new UserDto(
                        null,
                        "Mike",
                        "mike@mail.com")
        );
        itemDto = itemService.save(
                new ItemDto(
                        null,
                        "pen",
                        "blue",
                        true,
                        owner.getId(),
                        null),
                null,
                owner.getId()
        );
        var itemAllFieldsDto = new ItemAllFieldsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                true,
                owner.getId(),
                null,
                null,
                null,
                of()
        );

        BookingSavingDto bookingSavingDto = BookingSavingDto.builder()
                .id(1)
                .start(now())
                .end(now().plusHours(2))
                .itemId(1)
                .booker(1)
                .status(WAITING.name())
                .build();

        bookingAllFieldsDto = bookingService.save(
                bookingSavingDto,
                itemAllFieldsDto,
                booker.getId()
        );
    }

    @Test
    void saveTest() {
        var booking = entityManager
                .createQuery(
                        "SELECT booking " +
                                "FROM Booking booking",
                        Booking.class)
                .getSingleResult();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker().getId(),
                equalTo(bookingAllFieldsDto.getBooker().getId()));
        assertThat(booking.getItem().getId(),
                equalTo(bookingAllFieldsDto.getItem().getId()));
    }

    @Test
    void getAllBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                null,
                null,
                null);
        var booking = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.booker.id = :id",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .getResultList();
        assertThat(approved.get(0).getId(),
                equalTo(booking.get(0).getId()));
        assertThat(approved.size(),
                equalTo(booking.size()));
    }

    @Test
    void getUnknownBookingsTest() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            bookingService.getAllBookings(
                    bookingAllFieldsDto.getBooker().getId(),
                    "Super",
                    null,
                    null);
        });
        String expectedMessage = "Unknown state: Super";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getUnknownBookingsByOwnerIdTest() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "Super",
                null,
                null);
        });
        String expectedMessage = "Unknown state: Super";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getPASTBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "PAST",
                null,
                null);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getPastBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "PAST");
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getALLBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "ALL");
        assertThat(approved.size(),
                equalTo(1));
    }

    @Test
    void getCurrentBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "CURRENT",
                null,
                null);
        assertThat(approved.size(),
                equalTo(1));
    }

    @Test
    void getAnyBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                null,
                null,
                null);
        assertThat(approved.size(),
                equalTo(1));
    }

    @Test
    void getFutureBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "FUTURE",
                null,
                null);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getFuturePageBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "FUTURE",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getCurrentPageBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "CURRENT",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getAllPageBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "ALL",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getPastPageBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                "PAST",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getAnyPageBookingsTest() {
        var approved = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                null,
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getBookingsByOwnerIdStatusTest() {
        var bookings = bookingService.getBookingsByOwnerId(
                owner.getId(),
                APPROVED.name(),
                null,
                null);
        var approvedBookings = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", owner.getId())
                .setParameter("status", APPROVED)
                .getResultList();
        assertThat(bookings.size(),
                equalTo(approvedBookings.size()));
        assertThat(bookings.size(),
                equalTo(0));
    }

    @Test
    void getPastBookingsByOwnerIdTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "PAST",
                null,
                null);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getPastBookingsByOwnerIdPageTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "PAST",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getAllBookingsByOwnerIdTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "ALL",
                null,
                null);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getAllBookingsByOwnerIdPageTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "ALL",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getCurrentBookingsByOwnerIdTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "CURRENT",
                null,
                null);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getBookingsByOwnerIdPageTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "CURRENT",
                1,
                1);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getFutureBookingsByOwnerIdTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "FUTURE",
                null,
                null);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getFutureBookingsByOwnerIdPageTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "FUTURE",
                1,
                2);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getCurrentBookingsByOwnerIdPageTest() {
        var approved = bookingService.getBookingsByOwnerId(
                bookingAllFieldsDto.getBooker().getId(),
                "FUTURE",
                1,
                2);
        assertThat(approved.size(),
                equalTo(0));
    }

    @Test
    void getAllBookingsEmptyListTest() {
        var allBookings = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                APPROVED.name(),
                null,
                null);
        var approved = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.booker.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .setParameter("status", APPROVED)
                .getResultList();
        assertThat(allBookings.size(),
                equalTo(approved.size()));
        assertThat(allBookings.size(),
                equalTo(0));
    }

    @Test
    void getAllBookingsListTest() {
        var allBookings = bookingService.getAllBookings(
                bookingAllFieldsDto.getBooker().getId(),
                APPROVED.name());
        var approved = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.booker.id = :id AND booking.status = :status",
                        Booking.class)
                .setParameter("id", bookingAllFieldsDto.getBooker().getId())
                .setParameter("status", APPROVED)
                .getResultList();
        assertThat(allBookings.size(),
                equalTo(approved.size()));
        assertThat(allBookings.size(),
                equalTo(0));
    }

    @Test
    void getBookingsByOwnerIdTest() {
        var bookings = bookingService.getBookingsByOwnerId(
                owner.getId(),
                null,
                null,
                null);
        var booking = entityManager.createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "JOIN booking.item item " +
                                "WHERE item.owner.id = :id",
                        Booking.class)
                .setParameter("id", owner.getId())
                .getResultList();
        assertThat(bookings.get(0).getId(),
                equalTo(booking.get(0).getId()));
        assertThat(bookings.size(),
                equalTo(booking.size()));
    }

    @Test
    void getBookingByIdTest() {
        var approved = bookingService.getBookingById(
                bookingAllFieldsDto.getId(),
                bookingAllFieldsDto.getBooker().getId()
        );
        var booking = entityManager
                .createQuery(
                        "SELECT booking " +
                                "FROM Booking booking " +
                                "WHERE booking.id = :id AND booking.booker.id = :bookerId",
                        Booking.class)
                .setParameter("bookerId", bookingAllFieldsDto.getBooker().getId())
                .setParameter("id", bookingAllFieldsDto.getId())
                .getSingleResult();
        assertThat(approved.getItem().getId(),
                equalTo(booking.getItem().getId()));
        assertThat(approved.getStart(),
                equalTo(booking.getStart()));
        assertThat(approved.getId(),
                equalTo(booking.getId()));
    }

    @Test
    void approveBookingStateCanNotBeApprovedTest() {
        User user1 = new User(null, "user1", "test1@test.ru");
        User user2 = new User(null,"user2", "test2@test.ru");

        UserDto user1dto = UserMapper.toUserDto(user1);
        UserDto user2dto = UserMapper.toUserDto(user2);

        UserDto savedUser1 = userService.save(user1dto);
        UserDto savedUser2 = userService.save(user2dto);

        user1.setId(savedUser1.getId());
        user2.setId(savedUser2.getId());

        Item item1 = new Item(null, "item1", "description1", true, user1, null);
        ItemDto item1dto = ItemMapper.mapToItemDto(item1);
        ItemAllFieldsDto item1AllFieldsDto = ItemMapper.mapToItemAllFieldsDto(item1, null, null, null);
        ItemDto item1Dto = itemService.save(item1dto, null, savedUser1.getId());
        item1.setId(item1Dto.getId());

        Booking booking1 = Booking.builder()
                .id(null)
                .start(now())
                .end(now().plusHours(2))
                .item(item1)
                .booker(user2)
                .status(WAITING)
                .build();

        bookingRepository.save(booking1);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.approve(booking1.getId(),true, user2.getId());;
        });
        String expectedMessage = "There is no available approve for the user with id";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void approveBookingStateCanBeApprovedTest() {
        User user1 = new User(null, "user3", "test3@test.ru");
        User user2 = new User(null,"user4", "test4@test.ru");

        UserDto user1dto = UserMapper.toUserDto(user1);
        UserDto user2dto = UserMapper.toUserDto(user2);

        UserDto savedUser1 = userService.save(user1dto);
        UserDto savedUser2 = userService.save(user2dto);

        user1.setId(savedUser1.getId());
        user2.setId(savedUser2.getId());

        Item item1 = new Item(null, "item2", "description2", true, user1, null);
        ItemDto item1dto = ItemMapper.mapToItemDto(item1);
        ItemAllFieldsDto item1AllFieldsDto = ItemMapper.mapToItemAllFieldsDto(item1, null, null, null);
        ItemDto item1Dto = itemService.save(item1dto, null, savedUser1.getId());
        item1.setId(item1Dto.getId());

        Booking booking1 = Booking.builder()
                .id(null)
                .start(now())
                .end(now().plusHours(2))
                .item(item1)
                .booker(user2)
                .status(WAITING)
                .build();

        bookingRepository.save(booking1);

        List<BookingAllFieldsDto> bookings = bookingService.getBookingsByOwnerId(user1.getId(), "ALL",1, 1);
        bookingService.approve(booking1.getId(),true, user1.getId());
    }
}
