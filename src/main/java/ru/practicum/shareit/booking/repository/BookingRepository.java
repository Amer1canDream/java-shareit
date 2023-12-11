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

    List<Booking> findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(Integer itemId,
                                                                      Integer userId);

    Page<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner,
                                                            Pageable pageable);

    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner);

    //for booker
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

    List<Booking> findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(Integer itemId,
                                                                  BookingStatus bookingStatus,
                                                                  LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingStatus bookingState);

    Page<Booking> findBookingsByBookerIsAndStatusIsOrderByStartDesc(User booker,
                                                                    BookingStatus bookingState,
                                                                    Pageable pageable);

    Page<Booking> findBookingsByBookerIsOrderByStartDesc(User booker,
                                                         Pageable pageable);

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> getOwnerAll(Integer ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerFuture(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId " +
            "AND b.start <= :currentTime AND b.end >= :currentTime ORDER BY b.start DESC ")
    List<Booking> getOwnerCurrent(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    List<Booking> getAllByItemOwnerIdAndStatus(Integer ownerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end < :currentTime")
    List<Booking> getOwnerPast(@Param("userId") int userId, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    List<Booking> getAllByItemOwnerIdOrderByStartDesc(Integer userId);

    Booking getFirstByItemIdAndEndBeforeOrderByEndDesc(Integer itemId, LocalDateTime end);
    Booking getTopByItemIdAndStartAfterOrderByStartAsc(Integer itemId, LocalDateTime start);
}
