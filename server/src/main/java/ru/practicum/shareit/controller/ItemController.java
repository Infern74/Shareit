package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.ItemWithBookingsDto;
import ru.practicum.shareit.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("POST /items - Создание вещи: {}, владелец ID: {}", itemDto, ownerId);
        ItemDto result = itemService.createItem(itemDto, ownerId);
        log.info("Вещь создана успешно: {}", result);
        return result;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Long id,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("PATCH /items/{} - Обновление вещи: {}, владелец ID: {}", id, itemDto, ownerId);
        ItemDto result = itemService.updateItem(id, itemDto, ownerId);
        log.info("Вещь обновлена успешно: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public Object getItemById(@PathVariable Long id,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("GET /items/{} - Получение вещи по ID", id);

        ItemDto itemDto = itemService.getItemById(id, userId);

        if (userId != null && itemDto != null && itemDto.getOwnerId().equals(userId)) {
            ItemWithBookingsDto result = itemService.getItemWithBookings(id, userId);
            log.info("Найдена вещь с бронированиями: {}", result);
            return result;
        }

        log.info("Найдена вещь: {}", itemDto);
        return itemDto;
    }

    @GetMapping
    public List<ItemWithBookingsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /items - Получение всех вещей владельца ID: {}", ownerId);
        List<ItemWithBookingsDto> result = itemService.getItemsWithBookingsByOwner(ownerId);
        log.info("Найдено {} вещей владельца ID: {}", result.size(), ownerId);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET /items/search - Поиск вещей по тексту: '{}'", text);
        List<ItemDto> result = itemService.searchItems(text);
        log.info("Найдено {} вещей по запросу: '{}'", result.size(), text);
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items/{}/comment - Добавление комментария: {}, пользователь ID: {}", itemId, commentDto, userId);
        CommentDto result = itemService.addComment(itemId, commentDto, userId);
        log.info("Комментарий добавлен: {}", result);
        return result;
    }
}