package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
  private final BookingService bookingService;

  @PostMapping
  public BookingResponseDto createBooking(
          @Valid @RequestBody BookingDto bookingDto,
          @RequestHeader("X-Sharer-User-Id") Long bookerId) {
    log.info("Creating booking for user ID: {}", bookerId);
    return bookingService.createBooking(bookingDto, bookerId);
  }

  @PatchMapping("/{bookingId}")
  public BookingResponseDto updateBookingStatus(
          @PathVariable Long bookingId,
          @RequestParam Boolean approved,
          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
    log.info("Updating booking ID: {} status to {} by user ID: {}", bookingId, approved, ownerId);
    return bookingService.updateBookingStatus(bookingId, approved, ownerId);
  }

  @GetMapping("/{bookingId}")
  public BookingResponseDto getBookingById(
          @PathVariable Long bookingId,
          @RequestHeader("X-Sharer-User-Id") Long userId) {
    log.info("Getting booking ID: {} for user ID: {}", bookingId, userId);
    return bookingService.getBookingById(bookingId, userId);
  }

  @GetMapping
  public List<BookingResponseDto> getUserBookings(
          @RequestHeader("X-Sharer-User-Id") Long bookerId,
          @RequestParam(defaultValue = "ALL") BookingState state,
          @RequestParam(defaultValue = "0") int from,
          @RequestParam(defaultValue = "10") int size) {
    log.info("Getting bookings for user ID: {} with state: {}", bookerId, state);
    return bookingService.getUserBookings(bookerId, state, from, size);
  }

  @GetMapping("/owner")
  public List<BookingResponseDto> getOwnerBookings(
          @RequestHeader("X-Sharer-User-Id") Long ownerId,
          @RequestParam(defaultValue = "ALL") BookingState state,
          @RequestParam(defaultValue = "0") int from,
          @RequestParam(defaultValue = "10") int size) {
    log.info("Getting bookings for owner ID: {} with state: {}", ownerId, state);
    return bookingService.getOwnerBookings(ownerId, state, from, size);
  }
}
