package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.booking.BookingShortDto;
import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.ItemWithBookingsDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.Comment;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    private Item getItemByIdOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Вещь с ID %d не найдена", id);
                    log.error(errorMessage);
                    return new NoSuchElementException(errorMessage);
                });
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь с ID %d не найден", userId);
                    log.error(errorMessage);
                    return new NoSuchElementException(errorMessage);
                });
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.info("Создание вещи: {}, для владельца с ID: {}", itemDto, ownerId);
        getUserByIdOrThrow(ownerId);

        if (itemDto.getRequestId() != null) {
            Long requestId = itemDto.getRequestId();
            requestRepository.findById(requestId)
                    .orElseThrow(() -> {
                        String errorMessage = String.format("Запрос с ID %d не найден", requestId);
                        log.error(errorMessage);
                        return new NoSuchElementException(errorMessage);
                    });
        }

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);

        log.info("Вещь успешно создана: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId) {
        log.info("Обновление вещи с ID: {}, данные для обновления: {}, владелец ID: {}", id, itemDto, ownerId);

        Item existingItem = getItemByIdOrThrow(id);

        if (!ownerId.equals(existingItem.getOwnerId())) {
            log.warn("Попытка обновления вещи с ID: {} пользователем с ID: {}, который не является владельцем", id, ownerId);
            throw new SecurityException("Only owner can update item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
            log.debug("Обновлено название вещи с ID: {}", id);
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
            log.debug("Обновлено описание вещи с ID: {}", id);
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
            log.debug("Обновлен статус доступности вещи с ID: {}", id);
        }

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Вещь с ID: {} успешно обновлена: {}", id, updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id, Long userId) {
        log.debug("Поиск вещи по ID: {}", id);
        Item item = getItemByIdOrThrow(id);

        List<Comment> comments = commentRepository.findByItemId(id);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingsDto> getItemsWithBookingsByOwner(Long ownerId) {
        log.info("Получение всех вещей с бронированиями владельца с ID: {}", ownerId);
        getUserByIdOrThrow(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> bookingsMap = bookingRepository
                .findApprovedBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsMap = commentRepository
                .findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemWithBookingsDto> result = items.stream()
                .map(item -> {
                    ItemWithBookingsDto itemDto = new ItemWithBookingsDto();
                    itemDto.setId(item.getId());
                    itemDto.setName(item.getName());
                    itemDto.setDescription(item.getDescription());
                    itemDto.setAvailable(item.getAvailable());
                    itemDto.setRequestId(item.getRequestId());

                    List<Booking> itemBookings = bookingsMap.getOrDefault(item.getId(), Collections.emptyList());

                    Booking lastBooking = itemBookings.stream()
                            .filter(b -> b.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null);

                    Booking nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    if (lastBooking != null) {
                        itemDto.setLastBooking(new BookingShortDto(
                                lastBooking.getId(),
                                lastBooking.getBooker().getId(),
                                lastBooking.getStart(),
                                lastBooking.getEnd()
                        ));
                    }

                    if (nextBooking != null) {
                        itemDto.setNextBooking(new BookingShortDto(
                                nextBooking.getId(),
                                nextBooking.getBooker().getId(),
                                nextBooking.getStart(),
                                nextBooking.getEnd()
                        ));
                    }

                    List<Comment> comments = commentsMap.getOrDefault(item.getId(), Collections.emptyList());
                    itemDto.setComments(comments.stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList()));

                    return itemDto;
                })
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей для владельца с ID: {}", result.size(), ownerId);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        List<Item> foundItems = itemRepository.searchAvailable(text);
        List<ItemDto> result = foundItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей по запросу: '{}'", result.size(), text);
        return result;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        log.info("Добавление комментария к вещи ID: {}, пользователем ID: {}", itemId, userId);

        Item item = getItemByIdOrThrow(itemId);
        User author = getUserByIdOrThrow(userId);

        LocalDateTime now = LocalDateTime.now();

        boolean hasCompletedBookings = bookingRepository.existsCompletedBooking(itemId, userId, now);

        if (!hasCompletedBookings) {
            throw new BadRequestException("User can only comment on items they have booked");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author, now);
        comment = commentRepository.save(comment);
        log.info("Комментарий добавлен: {}", comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingsDto getItemWithBookings(Long itemId, Long userId) {
        log.debug("Поиск вещи с бронированиями по ID: {}", itemId);
        Item item = getItemByIdOrThrow(itemId);

        if (!item.getOwnerId().equals(userId)) {
            throw new NoSuchElementException("Only owner can view booking information");
        }

        ItemWithBookingsDto itemDto = new ItemWithBookingsDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId());

        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, now);
        List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, now);

        if (!lastBookings.isEmpty()) {
            Booking lastBooking = lastBookings.getFirst();
            itemDto.setLastBooking(new BookingShortDto(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd()
            ));
        }

        if (!nextBookings.isEmpty()) {
            Booking nextBooking = nextBookings.getFirst();
            itemDto.setNextBooking(new BookingShortDto(
                    nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd()
            ));
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    public Map<Long, List<ItemDto>> getItemsByRequestIds(List<Long> requestIds) {
        List<Item> items = itemRepository.findByRequestIdIn(requestIds);
        return items.stream()
                .collect(Collectors.groupingBy(
                        Item::getRequestId,
                        Collectors.mapping(ItemMapper::toItemDto, Collectors.toList())
                ));
    }
}