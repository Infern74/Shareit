package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemResponseDto;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.model.ItemRequest;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.ItemRequestRepository;
import ru.practicum.shareit.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto requestDto, Long userId) {
        User requestor = getUserByIdOrThrow(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requestor);
        request = requestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(Long userId) {
        getUserByIdOrThrow(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return enrichRequestsWithItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        getUserByIdOrThrow(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId, pageable);
        return enrichRequestsWithItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        getUserByIdOrThrow(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Запрос с ID %d не найден", requestId);
                    log.error(errorMessage);
                    return new NoSuchElementException(errorMessage);
                });
        return enrichRequestWithItems(request);
    }

    private List<ItemRequestDto> enrichRequestsWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemsByRequest = itemService.getItemsByRequestIds(requestIds);

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
                    List<ItemDto> items = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList());
                    dto.setItems(items.stream()
                            .map(item -> new ItemResponseDto(item.getId(), item.getName(), item.getOwnerId()))
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ItemRequestDto enrichRequestWithItems(ItemRequest request) {
        Map<Long, List<ItemDto>> itemsByRequest = itemService.getItemsByRequestIds(List.of(request.getId()));
        List<ItemDto> items = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList());

        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
        dto.setItems(items.stream()
                .map(item -> new ItemResponseDto(item.getId(), item.getName(), item.getOwnerId()))
                .collect(Collectors.toList()));
        return dto;
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь с ID %d не найден", userId);
                    log.error(errorMessage);
                    return new NoSuchElementException(errorMessage);
                });
    }
}