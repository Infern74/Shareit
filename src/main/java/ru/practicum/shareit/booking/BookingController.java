package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /bookings - Создание бронирования: {}, пользователь ID: {}", bookingDto, userId);
        BookingResponseDto result = bookingService.createBooking(bookingDto, userId);
        log.info("Бронирование создано: {}", result);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(@PathVariable Long bookingId,
                                                  @RequestParam Boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{} - Обновление статуса: {}, пользователь ID: {}", bookingId, approved, userId);
        BookingResponseDto result = bookingService.updateBookingStatus(bookingId, approved, userId);
        log.info("Статус бронирования обновлен: {}", result);
        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{} - Получение бронирования, пользователь ID: {}", bookingId, userId);
        BookingResponseDto result = bookingService.getBookingById(bookingId, userId);
        log.info("Найдено бронирование: {}", result);
        return result;
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("GET /bookings - Получение бронирований пользователя ID: {}, состояние: {}", userId, state);
        List<BookingResponseDto> result = bookingService.getUserBookings(userId, state, from, size);
        log.info("Найдено {} бронирований", result.size());
        return result;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("GET /bookings/owner - Получение бронирований владельца ID: {}, состояние: {}", userId, state);
        List<BookingResponseDto> result = bookingService.getOwnerBookings(userId, state, from, size);
        log.info("Найдено {} бронирований", result.size());
        return result;
    }
}