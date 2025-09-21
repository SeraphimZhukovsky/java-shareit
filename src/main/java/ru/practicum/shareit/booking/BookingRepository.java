package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingRepository {
  private final Map<Long, Booking> bookings = new HashMap<>();
  private Long nextId = 1L;
  private final ItemRepository itemRepository = new ItemRepository();

  public Booking save(Booking booking) {
    booking.setId(nextId++);
    bookings.put(booking.getId(), booking);
    return booking;
  }

  public Optional<Booking> findById(Long id) {
    return Optional.ofNullable(bookings.get(id));
  }

  public List<Booking> findByBookerId(Long bookerId) {
    return bookings.values().stream()
            .filter(booking -> booking.getBookerId().equals(bookerId))
            .collect(Collectors.toList());
  }

  public List<Booking> findByItemOwnerId(Long ownerId) {
    return bookings.values().stream()
            .filter(booking -> {
              Optional<Item> itemOptional = itemRepository.findById(booking.getItemId());
              return itemOptional.isPresent() && itemOptional.get().getOwnerId().equals(ownerId);
            })
            .collect(Collectors.toList());
  }

  public List<Booking> findByItemId(Long itemId) {
    return bookings.values().stream()
            .filter(booking -> booking.getItemId().equals(itemId))
            .collect(Collectors.toList());
  }

  public Booking update(Booking booking) {
    bookings.put(booking.getId(), booking);
    return booking;
  }
}
