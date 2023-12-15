package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemAllFieldsDto extends ItemDto {
    private List<CommentDto> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;

    public ItemAllFieldsDto(Integer id,
                            String name,
                            String description,
                            Boolean available,
                            Integer ownerId,
                            Integer requestId,
                            BookingDto lastBooking,
                            BookingDto nextBooking,
                            List<CommentDto> comments) {
        super(id, name, description, available, ownerId, requestId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
