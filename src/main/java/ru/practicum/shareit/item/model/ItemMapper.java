package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingComparator;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDto.ItemBookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ItemDto> toItemDtoList(List<Item> items, List<Comment> comments, List<Booking> bookings) {

        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            Booking nextBooking = bookings.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                    .filter(b -> b.getStartBooking().isAfter(LocalDateTime.now()))
                    .min(new BookingComparator())
                    .orElse(null);
            Booking lastBooking = bookings.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                    .filter(b -> b.getStartBooking().isBefore(LocalDateTime.now())
                            || b.getEndBooking().isBefore(LocalDateTime.now()))
                    .max(new BookingComparator())
                    .orElse(null);

            itemsDto.add(toItemDto(i,
                    comments.stream()
                            .filter(c -> c.getItem().getId().equals(i.getId()))
                            .collect(Collectors.toList()),
                    nextBooking,
                    lastBooking));
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

    public List<ItemDto> toItemDtoListForUser(List<Item> items, List<Comment> comments) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(toItemDtoForUser(i,
                    comments.stream()
                            .filter(c -> c.getItem().getId().equals(i.getId()))
                            .collect(Collectors.toList())));
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

