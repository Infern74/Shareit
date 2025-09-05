package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.request.ItemRequestDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto requestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /requests - создание запроса: {}, пользователь ID: {}", requestDto, userId);
        return itemRequestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests - получение запросов пользователя ID: {}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("GET /requests/all - получение всех запросов, пользователь ID: {}", userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} - получение запроса, пользователь ID: {}", requestId, userId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}