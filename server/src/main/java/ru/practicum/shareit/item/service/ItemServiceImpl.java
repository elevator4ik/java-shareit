package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingComparator;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemIncomeDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ItemServiceImpl implements ItemService {

    final CommentsRepository commentsRepository;
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final ItemMapper itemMapper;
    final CommentMapper commentMapper;

    @Override
    public ItemDto createItem(int userId, ItemIncomeDto itemDto) {

        log.info("Start to create item {}", itemDto.getName());

        Item item = itemMapper.toItem(itemDto, getUserFromRepo(userId));

        return itemMapper.toItemDtoForUser(itemRepository.saveAndFlush(item), getComments(item));
    }

    @Override
    public ItemDto updateItem(int id, int userId, ItemIncomeDto itemDto) {

        log.info("Start to update item with id {}", id);

        Item item = itemRepository.findByOwnerIdAndId(userId, id)
                .orElse(getItemFromRepo(id));

        if (userId == item.getOwner().getId()) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return itemMapper.toItemDto(item, getComments(item), null, null);
        } else {
            throw new NotFoundException("Not owner trying to update Item");
        }
    }

    @Override
    public ItemDto getItem(int id, int userId) {
        log.info("Start to getting item with id {}", id);

        Item item = itemRepository.findByOwnerIdAndId(userId, id)
                .orElse(getItemFromRepo(id));
        List<Booking> bookings = bookingRepository.findAllByItemAndStatus(item, BookingEnum.APPROVED);

        if (userId == item.getOwner().getId()) {
            if (bookings.isEmpty()) {
                return itemMapper.toItemDto(item, getComments(item), null, null);
            } else {
                return itemMapper.toItemDto(item,
                        getComments(item),
                        getNextBooking(bookings),
                        getLastBooking(bookings));
            }
        } else {
            return itemMapper.toItemDtoForUser(item, getComments(item));
        }
    }

    @Override
    public List<ItemDto> getItemsOfOwner(int userId) {

        log.info("Start to getting items of owner with id {}", userId);
        Map<Integer, Booking> nextBookings = new HashMap<>();
        Map<Integer, Booking> lastBookings = new HashMap<>();
        Map<Integer, List<Comment>> itemsComments = new HashMap<>();
        List<Item> list = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findAllByItemIn(list,
                Sort.by(Sort.Direction.DESC, "startBooking"));

        getNextAndLastBookings(list, bookings, nextBookings, lastBookings);

        List<Comment> comments = commentsRepository.findAllByItemIn(list);

        getCommentsMap(list, comments, itemsComments);

        return itemMapper.toItemDtoList(list, nextBookings, lastBookings, itemsComments);
    }

    @Override
    public List<ItemDto> searchItem(int userId, String text) {

        log.info("Start to searching item which contains {}", text);
        Map<Integer, List<Comment>> itemsComments = new HashMap<>();
        List<ItemDto> itemsDto = new ArrayList<>();
        if (!text.isEmpty()) {
            List<Item> items = itemRepository.search(text);

            if (!items.isEmpty()) {
                List<Comment> comments = commentsRepository.findAllByItemIn(items);

                getCommentsMap(items, comments, itemsComments);

                itemsDto = itemMapper.toItemDtoListForUser(items, itemsComments);
            }
        }
        return itemsDto;
    }

    @Override
    public CommentDto createComment(int userId, int itemId, CommentDto commentDto) {
        log.info("Start to create comment to item {}", itemId);

        User user = getUserFromRepo(userId);
        Item item = getItemFromRepo(itemId);
        bookingRepository.findFirstByItemAndBookerIdAndStatusAndEndBookingBeforeOrderByStartBooking(
                item, userId, BookingEnum.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Booking still not end or user+item not match"));
        Comment comment = commentMapper.toComment(commentDto, user, item);

        return commentMapper.toCommentDto(commentsRepository.saveAndFlush(comment));
    }

    private Item getItemFromRepo(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    private User getUserFromRepo(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
    }

    private List<Comment> getComments(Item item) {
        return commentsRepository.findAllByItem(item);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStartBooking().isAfter(LocalDateTime.now()))
                .min(new BookingComparator())
                .orElse(null);
    }

    private Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStartBooking().isBefore(LocalDateTime.now())
                        || b.getEndBooking().isBefore(LocalDateTime.now()))
                .max(new BookingComparator())
                .orElse(null);
    }

    private void getNextAndLastBookings(List<Item> list,
                                        List<Booking> bookings,
                                        Map<Integer, Booking> nextBookings,
                                        Map<Integer, Booking> lastBookings) {
        for (Item i : list) {
            Booking nextBooking = bookings.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                    .filter(b -> b.getStartBooking().isAfter(LocalDateTime.now()))
                    .min(new BookingComparator())
                    .orElse(null);
            nextBookings.put(i.getId(), nextBooking);
            Booking lastBooking = bookings.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .filter(b -> b.getStatus().equals(BookingEnum.APPROVED))
                    .filter(b -> b.getStartBooking().isBefore(LocalDateTime.now())
                            || b.getEndBooking().isBefore(LocalDateTime.now()))
                    .max(new BookingComparator())
                    .orElse(null);
            lastBookings.put(i.getId(), lastBooking);
        }
    }

    private void getCommentsMap(List<Item> list, List<Comment> comments, Map<Integer, List<Comment>> itemsComments) {
        for (Item i : list) {
            itemsComments.put(i.getId(), comments.stream()
                    .filter(c -> c.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList()));
        }
    }
}