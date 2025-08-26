package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item createItem(Item item, Long ownerId) {
        userService.getUserById(ownerId);
        item.setOwnerId(ownerId);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long id, Item item, Long ownerId) {
        Item existingItem = getItemById(id);
        if (!ownerId.equals(existingItem.getOwnerId())) {
            throw new SecurityException("Only owner can update item");
        }

        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new IllegalArgumentException("Name cannot be blank");
            }
            existingItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            if (item.getDescription().isBlank()) {
                throw new IllegalArgumentException("Description cannot be blank");
            }
            existingItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchAvailable(text);
    }
}