package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (ownerId.equals(item.getOwnerId())) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Item> searchAvailable(String text) {
        List<Item> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        String lowerText = text.toLowerCase();
        for (Item item : items.values()) {
            if (Boolean.TRUE.equals(item.getAvailable()) &&
                    (item.getName().toLowerCase().contains(lowerText) ||
                            item.getDescription().toLowerCase().contains(lowerText))) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }
}