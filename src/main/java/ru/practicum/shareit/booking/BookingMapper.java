package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

public class BookingMapper {
  public static BookingDto toBookingDto(Booking booking) {
    return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getItemId(),
            booking.getBookerId(),
            booking.getStatus()
    );
  }

  public static BookingShortDto toBookingShortDto(Booking booking) {
    return new BookingShortDto(
            booking.getId(),
            booking.getBookerId(),
            booking.getStart(),
            booking.getEnd()
    );
  }

  public static Booking toBooking(BookingDto bookingDto) {
    return new Booking(
            bookingDto.getId(),
            bookingDto.getStart(),
            bookingDto.getEnd(),
            bookingDto.getItemId(),
            bookingDto.getBookerId(),
            bookingDto.getStatus()
    );
  }
}
