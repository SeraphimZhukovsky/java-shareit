package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
  private final BookingRepository bookingRepository = new BookingRepository();
  private final ItemRepository itemRepository = new ItemRepository();
  private final UserService userService;
  private final ItemService itemService;

  public BookingService(UserService userService, ItemService itemService) {
    this.userService = userService;
    this.itemService = itemService;
  }

  public BookingDto createBooking(BookingDto bookingDto, Long bookerId) {
    userService.getUserById(bookerId);

    Item item = itemRepository.findById(bookingDto.getItemId())
            .orElseThrow(() -> new NotFoundException("Item not found"));

    if (!item.getAvailable()) {
      throw new ValidationException("Item is not available for booking");
    }

    if (item.getOwnerId().equals(bookerId)) {
      throw new NotFoundException("Cannot book your own item");
    }

    validateBookingDates(bookingDto.getStart(), bookingDto.getEnd());

    Booking booking = BookingMapper.toBooking(bookingDto);
    booking.setBookerId(bookerId);
    booking.setStatus(BookingStatus.WAITING);

    Booking savedBooking = bookingRepository.save(booking);
    return BookingMapper.toBookingDto(savedBooking);
  }

  public BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

    Item item = itemRepository.findById(booking.getItemId())
            .orElseThrow(() -> new NotFoundException("Item not found"));

    if (!item.getOwnerId().equals(ownerId)) {
      throw new AccessDeniedException("Only item owner can approve booking");
    }

    if (booking.getStatus() != BookingStatus.WAITING) {
      throw new ValidationException("Booking is already processed");
    }

    booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
    Booking updatedBooking = bookingRepository.update(booking);
    return BookingMapper.toBookingDto(updatedBooking);
  }

  public BookingDto getBookingById(Long bookingId, Long userId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

    Item item = itemRepository.findById(booking.getItemId())
            .orElseThrow(() -> new NotFoundException("Item not found"));

    boolean isBooker = booking.getBookerId().equals(userId);
    boolean isOwner = item.getOwnerId().equals(userId);

    if (!isBooker && !isOwner) {
      throw new NotFoundException("Access denied: user is not booker or owner");
    }

    return BookingMapper.toBookingDto(booking);
  }

  public List<BookingDto> getUserBookings(Long userId, String state) {
    userService.getUserById(userId);

    List<Booking> bookings = bookingRepository.findByBookerId(userId);

    return filterBookingsByState(bookings, state).stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
  }

  public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
    userService.getUserById(ownerId);

    List<Booking> bookings = bookingRepository.findByItemOwnerId(ownerId);

    return filterBookingsByState(bookings, state).stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
  }

  private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
    if (state == null || state.equals("ALL")) {
      return bookings;
    }

    return bookings.stream()
            .filter(booking -> {
              switch (state.toUpperCase()) {
                case "CURRENT":
                  return booking.getStart().isBefore(LocalDateTime.now()) &&
                          booking.getEnd().isAfter(LocalDateTime.now());
                case "PAST":
                  return booking.getEnd().isBefore(LocalDateTime.now());
                case "FUTURE":
                  return booking.getStart().isAfter(LocalDateTime.now());
                case "WAITING":
                  return booking.getStatus() == BookingStatus.WAITING;
                case "REJECTED":
                  return booking.getStatus() == BookingStatus.REJECTED;
                default:
                  return true;
              }
            })
            .collect(Collectors.toList());
  }

  private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
    if (end.isBefore(start) || end.equals(start)) {
      throw new ValidationException("End date must be after start date");
    }

    if (start.isBefore(LocalDateTime.now())) {
      throw new ValidationException("Start date cannot be in the past");
    }
  }
}
