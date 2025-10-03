package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  @Override
  @Transactional
  public BookingResponseDto createBooking(BookingDto bookingDto, Long bookerId) {
    User booker = userRepository.findById(bookerId)
            .orElseThrow(() -> new NotFoundException("User with id " + bookerId + " not found"));

    Item item = itemRepository.findById(bookingDto.getItemId())
            .orElseThrow(() -> new NotFoundException("Item with id " + bookingDto.getItemId() + " not found"));

    if (!item.getAvailable()) {
      throw new ValidationException("Item is not available for booking");
    }

    if (item.getOwnerId().equals(bookerId)) {
      throw new NotFoundException("Owner cannot book his own item");
    }

    if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
            bookingDto.getStart().isEqual(bookingDto.getEnd())) {
      throw new ValidationException("Invalid booking dates");
    }

    Booking booking = new Booking();
    booking.setStart(bookingDto.getStart());
    booking.setEnd(bookingDto.getEnd());
    booking.setItem(item);
    booking.setBooker(booker);
    booking.setStatus(BookingStatus.WAITING);

    Booking savedBooking = bookingRepository.save(booking);
    log.info("Booking created with ID: {}", savedBooking.getId());

    return toBookingResponseDto(savedBooking);
  }

  @Override
  @Transactional
  public BookingResponseDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

    if (!booking.getItem().getOwnerId().equals(ownerId)) {
      throw new AccessDeniedException("Only item owner can update booking status");
    }

    if (booking.getStatus() != BookingStatus.WAITING) {
      throw new ValidationException("Booking status cannot be changed");
    }

    booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
    Booking updatedBooking = bookingRepository.save(booking);
    log.info("Booking ID: {} status updated to: {}", bookingId, updatedBooking.getStatus());

    return toBookingResponseDto(updatedBooking);
  }

  @Override
  public BookingResponseDto getBookingById(Long bookingId, Long userId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

    if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
      throw new AccessDeniedException("Access to booking denied");
    }

    return toBookingResponseDto(booking);
  }

  @Override
  public List<BookingResponseDto> getUserBookings(Long bookerId, BookingState state, int from, int size) {
    userRepository.findById(bookerId)
            .orElseThrow(() -> new NotFoundException("User with id " + bookerId + " not found"));

    User booker = new User();
    booker.setId(bookerId);

    PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
    List<Booking> bookings;

    switch (state) {
      case CURRENT:
        bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                bookerId, LocalDateTime.now(), LocalDateTime.now(), pageRequest.getSort());
        break;
      case PAST:
        bookings = bookingRepository.findByBookerIdAndEndBefore(
                bookerId, LocalDateTime.now(), pageRequest.getSort());
        break;
      case FUTURE:
        bookings = bookingRepository.findByBookerIdAndStartAfter(
                bookerId, LocalDateTime.now(), pageRequest.getSort());
        break;
      case WAITING:
      case REJECTED:
        BookingStatus status = BookingStatus.valueOf(state.name());
        bookings = bookingRepository.findByBookerIdAndStatus(bookerId, status, pageRequest.getSort());
        break;
      default:
        bookings = bookingRepository.findByBookerId(bookerId, pageRequest.getSort());
    }

    return bookings.stream()
            .map(this::toBookingResponseDto)
            .collect(Collectors.toList());
  }

  @Override
  public List<BookingResponseDto> getOwnerBookings(Long ownerId, BookingState state, int from, int size) {
    userRepository.findById(ownerId)
            .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

    PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
    List<Booking> bookings;

    switch (state) {
      case CURRENT:
        bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                ownerId, LocalDateTime.now(), LocalDateTime.now(), pageRequest.getSort());
        break;
      case PAST:
        bookings = bookingRepository.findByItemOwnerIdAndEndBefore(
                ownerId, LocalDateTime.now(), pageRequest.getSort());
        break;
      case FUTURE:
        bookings = bookingRepository.findByItemOwnerIdAndStartAfter(
                ownerId, LocalDateTime.now(), pageRequest.getSort());
        break;
      case WAITING:
      case REJECTED:
        BookingStatus status = BookingStatus.valueOf(state.name());
        bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, status, pageRequest.getSort());
        break;
      default:
        bookings = bookingRepository.findByItemOwnerId(ownerId, pageRequest.getSort());
    }

    return bookings.stream()
            .map(this::toBookingResponseDto)
            .collect(Collectors.toList());
  }

  private BookingResponseDto toBookingResponseDto(Booking booking) {
    BookingResponseDto.Booker booker = new BookingResponseDto.Booker(
            booking.getBooker().getId(),
            booking.getBooker().getName()
    );

    BookingResponseDto.Item item = new BookingResponseDto.Item(
            booking.getItem().getId(),
            booking.getItem().getName()
    );

    return new BookingResponseDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getStatus(),
            booker,
            item
    );
  }
}
