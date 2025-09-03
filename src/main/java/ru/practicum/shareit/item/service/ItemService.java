package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long id, Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);

    ItemWithBookingsDto getItemWithBookings(Long itemId, Long userId);

    List<ItemWithBookingsDto> getItemsWithBookingsByOwner(Long ownerId);
}

