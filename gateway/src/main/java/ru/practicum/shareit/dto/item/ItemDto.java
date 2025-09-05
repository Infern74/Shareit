package ru.practicum.shareit.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.dto.booking.BookingShortDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Available cannot be null")
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private List<CommentDto> comments;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}
