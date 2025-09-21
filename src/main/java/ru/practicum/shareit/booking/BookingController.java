package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  public BookingDto createBooking(@RequestBody BookingDto bookingDto,
                                  @RequestHeader(value = "X-Sharer-User-Id", required = false) Long bookerId) {
    if (bookerId == null) {
      throw new ValidationException("User ID header is required");
    }
    return bookingService.createBooking(bookingDto, bookerId);
  }

  @PatchMapping("/{bookingId}")
  public BookingDto approveBooking(@PathVariable Long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long ownerId) {
    return bookingService.approveBooking(bookingId, ownerId, approved);
  }

  @GetMapping("/{bookingId}")
  public BookingDto getBookingById(@PathVariable Long bookingId,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
    return bookingService.getBookingById(bookingId, userId);
  }

  @GetMapping
  public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
    return bookingService.getUserBookings(userId, state);
  }

  @GetMapping("/owner")
  public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
    return bookingService.getOwnerBookings(ownerId, state);
  }
}
