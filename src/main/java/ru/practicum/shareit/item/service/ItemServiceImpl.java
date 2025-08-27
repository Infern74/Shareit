package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    private Item getItemByIdOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Вещь с ID %d не найдена", id);
                    log.error(errorMessage);
                    return new NoSuchElementException(errorMessage);
                });
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.info("Создание вещи: {}, для владельца с ID: {}", itemDto, ownerId);
        userService.getUserById(ownerId);

        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item savedItem = itemRepository.save(item);

        log.info("Вещь успешно создана: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
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
    public ItemDto getItemById(Long id) {
        log.debug("Поиск вещи по ID: {}", id);
        Item item = getItemByIdOrThrow(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        log.info("Получение всех вещей владельца с ID: {}", ownerId);
        userService.getUserById(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        List<ItemDto> result = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей для владельца с ID: {}", result.size(), ownerId);
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);

        if (text == null || text.isBlank()) {
            log.debug("Пустой поисковый запрос, возвращаем пустой список");
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        List<Item> allItems = itemRepository.findAll();
        List<ItemDto> result = allItems.stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.debug("Найдено {} вещей по запросу: '{}'", result.size(), text);
        return result;
    }
}