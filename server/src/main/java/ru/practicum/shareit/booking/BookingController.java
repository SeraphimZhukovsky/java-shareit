package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
  private final BookingService bookingService;
  private final BookingRepository bookingRepository;
  private static final String USER_ID_HEADER = "X-Sharer-User-Id";

  @PostMapping
  public BookingDto createBooking(
          @Valid @RequestBody BookingRequestDto bookingRequestDto,
          @RequestHeader(USER_ID_HEADER) Long bookerId) {
    log.info("Creating booking for user ID: {}", bookerId);
    return bookingService.createBooking(bookingRequestDto, bookerId);
  }

  @PatchMapping("/{bookingId}")
  public BookingDto approveBooking(
          @PathVariable Long bookingId,
          @RequestParam Boolean approved,
          @RequestHeader(USER_ID_HEADER) Long ownerId) {
    log.info("Updating booking ID: {} status to {} by user ID: {}", bookingId, approved, ownerId);
    return bookingService.approveBooking(bookingId, approved, ownerId);
  }

  @GetMapping("/{bookingId}")
  public BookingDto getBookingById(
          @PathVariable Long bookingId,
          @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Getting booking ID: {} for user ID: {}", bookingId, userId);
    return bookingService.getBookingById(bookingId, userId);
  }

  @GetMapping
  public List<BookingDto> getUserBookings(
          @RequestHeader(USER_ID_HEADER) Long bookerId,
          @RequestParam(defaultValue = "ALL") BookingState state,
          @RequestParam(defaultValue = "0") int from,
          @RequestParam(defaultValue = "10") int size) {
    log.info("Getting bookings for user ID: {} with state: {}", bookerId, state);
    return bookingService.getUserBookings(bookerId, state, from, size);
  }

  @GetMapping("/owner")
  public List<BookingDto> getOwnerBookings(
          @RequestHeader(USER_ID_HEADER) Long ownerId,
          @RequestParam(defaultValue = "ALL") BookingState state,
          @RequestParam(defaultValue = "0") int from,
          @RequestParam(defaultValue = "10") int size) {
    log.info("Getting bookings for owner ID: {} with state: {}", ownerId, state);
    return bookingService.getOwnerBookings(ownerId, state, from, size);
  }
}