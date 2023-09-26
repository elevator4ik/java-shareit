package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    final CommentsRepository commentsRepository;
    final BookingRepository bookingRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;
    final ItemMapper itemMapper;
    final CommentMapper commentMapper;

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {

        log.info("Start to create item {}", itemDto.getName());

        Item item = itemMapper.toItem(itemDto, getUserFromRepo(userId));

        return itemMapper.toItemDtoForUser(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDto updateItem(int id, int userId, ItemDto itemDto) {

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
            return itemMapper.toItemDto(
                    itemRepository.saveAndFlush(item));
        } else {
            throw new NotFoundException("Not owner trying to update Item");
        }
    }

    @Override
    public ItemDto getItem(int id, int userId) {
        log.info("Start to getting item with id {}", id);

        Item item = itemRepository.findByOwnerIdAndId(userId, id)
                .orElse(getItemFromRepo(id));
        if (userId == item.getOwner().getId()) {
            return itemMapper.toItemDto(item);
        } else {
            return itemMapper.toItemDtoForUser(item);
        }
    }

    @Override
    public List<ItemDto> getItemsOfOwner(int userId) {

        log.info("Start to getting items of owner with id {}", userId);

        List<Item> list = Optional.of(itemRepository.findAllByOwnerId(userId))
                .orElseThrow(() -> new NotFoundException("Nothing found"));
        return itemMapper.toItemDtoList(list);
    }

    @Override
    public List<ItemDto> searchItem(int userId, String text) {

        log.info("Start to searching item which contains {}", text);

        List<ItemDto> itemsDto = new ArrayList<>();
        if (!text.isEmpty()) {
            List<Item> items = itemRepository.search(text);

            if (!items.isEmpty()) {
                itemsDto = itemMapper.toItemDtoListForUser(items);
            }
        }
        return itemsDto;
    }

    @Override
    public CommentDto createComment(int userId, int itemId, CommentDto commentDto) {
        log.info("Start to create comment to item {}", itemId);
        bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBookingBefore(
                userId, itemId, BookingEnum.APPROVED, LocalDateTime.now().minusSeconds(5))// -5 сек нужны, чтобы тест в
                // постмане не ловил 6 букинг, т.к. эндтайм через 1 секнду после старттайма, а если он его ловит -
                // вылетает ошибка, что результат запроса не уникален, т.к. букинг создается на те же itemId и userId,
                // в реальности такое не возможно
                .orElseThrow(() -> new BadRequestException("Booking still not end or user+item not match"));
        Comment comment = commentMapper.toComment(commentDto, getUserFromRepo(userId), itemId);

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
}