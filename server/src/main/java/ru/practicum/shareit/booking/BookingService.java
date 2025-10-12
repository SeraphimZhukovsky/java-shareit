package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
  BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId);

  BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId);

  BookingDto getBookingById(Long bookingId, Long userId);

  List<BookingDto> getUserBookings(Long bookerId, BookingState state, int from, int size);

  List<BookingDto> getOwnerBookings(Long ownerId, BookingState state, int from, int size);
}