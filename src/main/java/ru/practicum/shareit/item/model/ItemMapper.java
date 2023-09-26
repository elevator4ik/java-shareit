package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDto.ItemBookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemMapper {

    public Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequest(),
                null,
                null);
    }

    public ItemDto toItemDto(Item item) {

        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                nextBooking(item),
                lastBooking(item),
                toCommentDto(item.getComments()));

    }

    public List<ItemDto> toItemDtoList(List<Item> items) {

        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(toItemDto(i));
        }
        return itemsDto;
    }

    public ItemDto toItemDtoForUser(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                null,
                null,
                toCommentDto(item.getComments()));
    }

    public List<ItemDto> toItemDtoListForUser(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(toItemDtoForUser(i));
        }
        return itemsDto;
    }

    private ItemBookingDto lastBooking(Item item) {

        Booking lastBooking = item.getBookings()
                .stream()
                .sorted()
                .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                .filter(b -> b.getStartBooking().isBefore(LocalDateTime.now())
                        || b.getEndBooking().isBefore(LocalDateTime.now()))
                .max(new BookingComparator())
                .orElse(null);

        if (lastBooking != null) {
            return new ItemBookingDto(lastBooking.getId(),
                    lastBooking.getStartBooking(),
                    lastBooking.getEndBooking(),
                    lastBooking.getBookerId());
        } else {
            return null;
        }
    }

    private ItemBookingDto nextBooking(Item item) {
        Booking nextBooking = item.getBookings()
                .stream()
                .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                .filter(b -> b.getStartBooking().isAfter(LocalDateTime.now()))
                .min(new BookingComparator())
                .orElse(null);

        if (nextBooking != null) {
            return new ItemBookingDto(nextBooking.getId(), nextBooking.getStartBooking(), nextBooking.getEndBooking(), nextBooking.getBookerId());
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

    static class BookingComparator implements Comparator<Booking> {

        @Override
        public int compare(Booking o1, Booking o2) {
            return o1.getStartBooking().compareTo(o2.getStartBooking());
        }
    }

}

