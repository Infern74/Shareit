package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
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
    public ItemDto getItemById(@PathVariable Long id) {
        log.info("GET /items/{} - Получение вещи по ID", id);
        ItemDto result = itemService.getItemById(id);
        log.info("Найдена вещь: {}", result);
        return result;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /items - Получение всех вещей владельца ID: {}", ownerId);
        List<ItemDto> result = itemService.getItemsByOwner(ownerId);
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
}