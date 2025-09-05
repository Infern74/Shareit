package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.ItemWithBookingsDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long id, Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);

    ItemWithBookingsDto getItemWithBookings(Long itemId, Long userId);

    List<ItemWithBookingsDto> getItemsWithBookingsByOwner(Long ownerId);

    Map<Long, List<ItemDto>> getItemsByRequestIds(List<Long> requestIds);
}

