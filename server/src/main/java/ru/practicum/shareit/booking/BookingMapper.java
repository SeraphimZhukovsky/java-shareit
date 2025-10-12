package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

  public static Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
    Booking booking = new Booking();
    booking.setStart(bookingRequestDto.getStart());
    booking.setEnd(bookingRequestDto.getEnd());
    booking.setItem(item);
    booking.setBooker(booker);
    booking.setStatus(BookingStatus.WAITING);
    return booking;
  }

  public static BookingDto toBookingDto(Booking booking) {
    BookingDto.Booker booker = new BookingDto.Booker(
            booking.getBooker().getId(),
            booking.getBooker().getName()
    );

    BookingDto.Item item = new BookingDto.Item(
            booking.getItem().getId(),
            booking.getItem().getName()
    );

    return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getStatus(),
            booker,
            item
    );
  }
}