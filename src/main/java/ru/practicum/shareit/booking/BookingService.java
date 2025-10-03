package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
  BookingResponseDto createBooking(BookingDto bookingDto, Long bookerId);

  BookingResponseDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId);

  BookingResponseDto getBookingById(Long bookingId, Long userId);

  List<BookingResponseDto> getUserBookings(Long bookerId, BookingState state, int from, int size);

  List<BookingResponseDto> getOwnerBookings(Long ownerId, BookingState state, int from, int size);
}
