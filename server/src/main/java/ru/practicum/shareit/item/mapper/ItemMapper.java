package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .build();
    }

    public static ItemAllFieldsDto mapToItemAllFieldsDto(Item item,
                                                         Booking lastBooking,
                                                         Booking nextBooking,
                                                         List<CommentDto> comments) {
        return new ItemAllFieldsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null ? new BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null,
                nextBooking != null ? new BookingDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null,
                comments != null ? comments : List.of()
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(List<Comment> commentList, Booking lastBooking,
                                                          Booking nextBooking, Item item) {
        List<ItemDtoWithBooking.CommentDto> comments = commentList.stream()
                .map(comment -> {
                    ItemDtoWithBooking.CommentDto comment1 = new ItemDtoWithBooking.CommentDto();
                    comment1.setId(comment.getId());
                    comment1.setText(comment.getText());
                    comment1.setAuthorName(comment.getAuthor().getName());
                    comment1.setCreated(comment.getCreated());
                    return comment1;
                }).collect(Collectors.toList());

        ItemDtoWithBooking.BookingDto lstBooking = new ItemDtoWithBooking.BookingDto();
        if (lastBooking != null) {
            lstBooking.setId(lastBooking.getId());
            lstBooking.setBookerId(lastBooking.getBooker().getId());
        } else {
            lstBooking = null;
        }

        ItemDtoWithBooking.BookingDto nxtBooking = new ItemDtoWithBooking.BookingDto();
        if (nextBooking != null) {
            nxtBooking.setId(nextBooking.getId());
            nxtBooking.setBookerId(nextBooking.getBooker().getId());
        } else {
            nxtBooking = null;
        }
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lstBooking)
                .nextBooking(nxtBooking)
                .comments(comments)
                .build();
    }
}
