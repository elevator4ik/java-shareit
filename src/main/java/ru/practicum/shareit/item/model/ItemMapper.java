package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDto.ItemBookingDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class ItemMapper {

    public Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequest());
    }

    public ItemDto toItemDto(Item item, List<Comment> comments, Booking nextBooking, Booking lastBooking) {

        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                toBookingDto(nextBooking),
                toBookingDto(lastBooking),
                toCommentDto(comments));

    }

    public List<ItemDto> toItemDtoList(List<Item> items,
                                       Map<Integer, Booking> nextBookings,
                                       Map<Integer, Booking> lastBookings,
                                       Map<Integer, List<Comment>> itemsComments) {

        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {

            itemsDto.add(toItemDto(i,
                    itemsComments.get(i.getId()),
                    nextBookings.get(i.getId()),
                    lastBookings.get(i.getId())));
        }
        return itemsDto;
    }

    public ItemDto toItemDtoForUser(Item item, List<Comment> comments) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                null,
                null,
                toCommentDto(comments));
    }

    public List<ItemDto> toItemDtoListForUser(List<Item> items, Map<Integer, List<Comment>> comments) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(toItemDtoForUser(i,
                    comments.get(i.getId())));
        }
        return itemsDto;
    }

    private ItemBookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return new ItemBookingDto(booking.getId(),
                    booking.getStartBooking(),
                    booking.getEndBooking(),
                    booking.getBookerId());
        } else {
            return null;
        }
    }

    public List<CommentDto> toCommentDto(List<Comment> comment) {
        List<CommentDto> commentsDto = new ArrayList<>();
        if (comment != null && !comment.isEmpty()) {
            for (Comment c : comment) {
                commentsDto.add(new CommentDto(c.getId(),
                        c.getText(),
                        c.getAuthor().getName(),
                        c.getCreated()));
            }
        }
        return commentsDto;
    }
}

