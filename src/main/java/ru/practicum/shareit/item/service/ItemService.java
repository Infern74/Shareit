package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long ownerId);

    Item updateItem(Long id, Item item, Long ownerId);

    Item getItemById(Long id);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> searchItems(String text);
}

