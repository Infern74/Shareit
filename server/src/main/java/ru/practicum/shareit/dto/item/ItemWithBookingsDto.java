package ru.practicum.shareit.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.dto.booking.BookingShortDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingShortDto lastBooking = null;
    private BookingShortDto nextBooking = null;
    private List<CommentDto> comments;
}