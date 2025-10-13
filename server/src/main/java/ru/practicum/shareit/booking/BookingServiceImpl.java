package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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
  public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
    User booker = getUserById(bookerId);

    Item item = itemRepository.findById(bookingRequestDto.getItemId())
            .orElseThrow(() -> new NotFoundException("Item with id " + bookingRequestDto.getItemId() + " not found"));

    // Проверка доступности вещи
    if (!item.getAvailable()) {
      throw new ValidationException("Item is not available for booking");
    }

    // Проверка, что владелец не бронирует свою вещь
    if (item.getOwner().getId().equals(bookerId)) {
      throw new NotFoundException("Owner cannot book his own item");
    }

    Booking booking = BookingMapper.toBooking(bookingRequestDto, item, booker);

    Booking savedBooking = bookingRepository.save(booking);
    log.info("Booking created with ID: {}", savedBooking.getId());

    return BookingMapper.toBookingDto(savedBooking);
  }

  @Override
  @Transactional
  public BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
    Booking booking = getBookingById(bookingId);

    // Проверка, что пользователь - владелец вещи
    if (!booking.getItem().getOwner().getId().equals(ownerId)) {
      throw new AccessDeniedException("Only item owner can update booking status");
    }

    // Проверка, что бронирование еще ожидает подтверждения
    if (booking.getStatus() != BookingStatus.WAITING) {
      throw new ValidationException("Booking status cannot be changed");
    }

    booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
    Booking updatedBooking = bookingRepository.save(booking);
    log.info("Booking ID: {} status updated to: {}", bookingId, updatedBooking.getStatus());

    return BookingMapper.toBookingDto(updatedBooking);
  }

  @Override
  public BookingDto getBookingById(Long bookingId, Long userId) {
    Booking booking = getBookingById(bookingId);

    // Проверка прав доступа
    if (!booking.getBooker().getId().equals(userId) &&
            !booking.getItem().getOwner().getId().equals(userId)) {
      throw new AccessDeniedException("Access to booking denied");
    }

    return BookingMapper.toBookingDto(booking);
  }

  @Override
  public List<BookingDto> getUserBookings(Long bookerId, BookingState state, int from, int size) {
    getUserById(bookerId);

    Pageable pageable = PageRequest.of(from / size, size);
    List<Booking> bookings;

    switch (state) {
      case CURRENT:
        bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
        break;
      case PAST:
        bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                bookerId, LocalDateTime.now(), pageable);
        break;
      case FUTURE:
        bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                bookerId, LocalDateTime.now(), pageable);
        break;
      case WAITING:
      case REJECTED:
        BookingStatus status = BookingStatus.valueOf(state.name());
        bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, status, pageable);
        break;
      default: // ALL
        bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable);
    }

    return bookings.stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
  }

  @Override
  public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state, int from, int size) {
    getUserById(ownerId);

    Pageable pageable = PageRequest.of(from / size, size);
    List<Booking> bookings;

    switch (state) {
      case CURRENT:
        bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
        break;
      case PAST:
        bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                ownerId, LocalDateTime.now(), pageable);
        break;
      case FUTURE:
        bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                ownerId, LocalDateTime.now(), pageable);
        break;
      case WAITING:
      case REJECTED:
        BookingStatus status = BookingStatus.valueOf(state.name());
        bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status, pageable);
        break;
      default: // ALL
        bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
    }

    return bookings.stream()
            .map(BookingMapper::toBookingDto)
            .collect(Collectors.toList());
  }

  private User getUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
  }

  private Booking getBookingById(Long bookingId) {
    return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
  }
}