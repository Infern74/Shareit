package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.BookingResponseDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingDto bookingDto, Long bookerId) {
        log.info("Создание бронирования: {}, пользователем ID: {}", bookingDto, bookerId);

        User booker = getUserByIdOrThrow(bookerId);
        Item item = getItemByIdOrThrow(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available for booking");
        }

        if (item.getOwnerId().equals(bookerId)) {
            throw new NoSuchElementException("Owner cannot book their own item");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking = bookingRepository.save(booking);

        log.info("Бронирование создано: {}", booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Обновление статуса бронирования ID: {}, approved: {}, владелец ID: {}",
                bookingId, approved, ownerId);

        Booking booking = getBookingByIdOrThrow(bookingId);
        Item item = booking.getItem();

        if (!item.getOwnerId().equals(ownerId)) {
            throw new SecurityException("Only owner can update booking status");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking status already decided");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);

        log.info("Статус бронирования обновлен: {}", booking);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования ID: {}, пользователем ID: {}", bookingId, userId);

        Booking booking = getBookingByIdOrThrow(bookingId);
        Item item = booking.getItem();

        if (!booking.getBooker().getId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new NoSuchElementException("Only booker or owner can view booking");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size) {
        log.info("Получение бронирований пользователя ID: {}, состояние: {}", userId, state);

        getUserByIdOrThrow(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
            case "CURRENT" -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now, pageable);
            case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
            case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.WAITING, pageable);
            case "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.REJECTED, pageable);
            default -> throw new BadRequestException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int from, int size) {
        log.info("Получение бронирований владельца ID: {}, состояние: {}", ownerId, state);

        getUserByIdOrThrow(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
            case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    ownerId, now, now, pageable);
            case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable);
            case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable);
            case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId, BookingStatus.WAITING, pageable);
            case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId, BookingStatus.REJECTED, pageable);
            default -> throw new BadRequestException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private ru.practicum.shareit.model.User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    private ru.practicum.shareit.model.Item getItemByIdOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item with id " + itemId + " not found"));
    }

    private Booking getBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking with id " + bookingId + " not found"));
    }
}