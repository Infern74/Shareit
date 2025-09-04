package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto requestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /requests - создание запроса: {}, пользователь ID: {}", requestDto, userId);
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests - получение запросов пользователя ID: {}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("GET /requests/all - получение всех запросов, пользователь ID: {}", userId);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} - получение запроса, пользователь ID: {}", requestId, userId);
        return requestService.getRequestById(requestId, userId);
    }
}