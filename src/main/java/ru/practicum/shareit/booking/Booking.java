package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    @NotNull
    @NotBlank
    private Integer id;

    private Date start;

    private Date end;

    private Item item;

    private User booker;

    private BookingStatus status;
}
