package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.ItemDto;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("POST /items - Создание вещи: {}, владелец ID: {}", itemDto, ownerId);
        return itemClient.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Long id,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("PATCH /items/{} - Обновление вещи: {}, владелец ID: {}", id, itemDto, ownerId);
        return itemClient.updateItem(id, itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id,
                                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("GET /items/{} - Получение вещи по ID", id);
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /items - Получение всех вещей владельца ID: {}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("GET /items/search - Поиск вещей по тексту: '{}'", text);

        if (text == null || text.isBlank()) {
            log.debug("Пустой поисковый запрос, возвращаем пустой список");
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items/{}/comment - Добавление комментария: {}, пользователь ID: {}", itemId, commentDto, userId);
        return itemClient.addComment(itemId, commentDto, userId);
    }
}