package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                null
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(requestDto.getCreated() != null ? requestDto.getCreated() : java.time.LocalDateTime.now());
        return request;
    }
}