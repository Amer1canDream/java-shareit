package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    //for owner
    List<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner,
                                                                                     LocalDateTime startDateTime,
                                                                                     LocalDateTime endDateTime);

    Page<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner,
                                                                                     LocalDateTime startDateTime,
                                                                                     LocalDateTime endDateTime,
                                                                                     Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner,
                                                                       LocalDateTime localDateTime);

    Page<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner,
                                                                       LocalDateTime localDateTime,
                                                                       Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner,
                                                                      LocalDateTime localDateTime);

    Page<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner,
                                                                      LocalDateTime localDateTime,
                                                                      Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner,
                                                                       BookingStatus bookingState);

    Page<Booking> findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(User owner,
                                                                       BookingStatus bookingState,
                                                                       Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner,
                                                            Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner);


    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                                  LocalDateTime startDateTime,
                                                                                  LocalDateTime endDateTime);

    Page<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                                  LocalDateTime startDateTime,
                                                                                  LocalDateTime endDateTime,
                                                                                  Pageable pageable);

    List<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                        LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                        LocalDateTime localDateTime,
                                                                        Pageable pageable);

    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker,
                                                                     LocalDateTime localDateTime);

    Page<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker,
                                                                     LocalDateTime localDateTime,
                                                                     Pageable pageable);

    List<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingStatus bookingState);

    Page<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingStatus bookingState,
                                                                    Pageable pageable);

    Page<Booking> findBookingsByBookerIsOrderByStartDesc(User booker,
                                                         Pageable pageable);

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);

    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(Integer itemId, LocalDateTime end);

    Booking getTopByItemIdAndStartAfterOrderByStartAsc(Integer itemId, LocalDateTime start);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and status not like 'REJECTED' and b.start <= ?3 order by b.end desc")
    List<Booking> findLastBookingByItemId(Integer itemId, Integer userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and status not like 'REJECTED' and b.start >= ?3 order by b.start asc")
    List<Booking> findNextBookingByItemId(Integer itemId, Integer userId, LocalDateTime now);

}
