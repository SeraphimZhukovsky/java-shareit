package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
  private final ItemRepository itemRepository = new ItemRepository();
  private final BookingRepository bookingRepository = new BookingRepository();
  private final ItemRequestRepository requestRepository = new ItemRequestRepository();
  private final UserService userService;

  public ItemService(UserService userService) {
    this.userService = userService;
  }

  public ItemDto createItem(ItemDto itemDto, Long ownerId) {
    userService.getUserById(ownerId);

    if (itemDto.getRequestId() != null) {
      requestRepository.findById(itemDto.getRequestId())
              .orElseThrow(() -> new NotFoundException("Request not found"));
    }

    Item item = ItemMapper.toItem(itemDto, ownerId);
    Item savedItem = itemRepository.save(item);
    return ItemMapper.toItemDto(savedItem);
  }

  public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
    Item existingItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item not found"));

    if (!existingItem.getOwnerId().equals(ownerId)) {
      throw new AccessDeniedException("Access denied");
    }

    if (itemDto.getName() != null) {
      if (itemDto.getName().isBlank()) {
        throw new ValidationException("Name cannot be blank");
      }
      existingItem.setName(itemDto.getName());
    }

    if (itemDto.getDescription() != null) {
      if (itemDto.getDescription().isBlank()) {
        throw new ValidationException("Description cannot be blank");
      }
      existingItem.setDescription(itemDto.getDescription());
    }

    if (itemDto.getAvailable() != null) {
      existingItem.setAvailable(itemDto.getAvailable());
    }

    Item updatedItem = itemRepository.update(existingItem);
    return ItemMapper.toItemDto(updatedItem);
  }

  public ItemDto getItemById(Long itemId, Long userId) {
    Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item not found"));

    ItemDto itemDto = ItemMapper.toItemDto(item);

    if (item.getOwnerId().equals(userId)) {
      addBookingInfo(itemDto);
    }

    return itemDto;
  }

  private void addBookingInfo(ItemDto itemDto) {
    List<Booking> itemBookings = bookingRepository.findByItemId(itemDto.getId());

    // Последнее завершенное бронирование
    Booking lastBooking = itemBookings.stream()
            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()) &&
                    booking.getStatus() == BookingStatus.APPROVED)
            .max((b1, b2) -> b2.getEnd().compareTo(b1.getEnd()))
            .orElse(null);

    // Следующее бронирование
    Booking nextBooking = itemBookings.stream()
            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) &&
                    booking.getStatus() == BookingStatus.APPROVED)
            .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
            .orElse(null);

    // Устанавливаем найденные бронирования в DTO
    if (lastBooking != null) {
      itemDto.setLastBooking(new BookingShortDto(
              lastBooking.getId(),
              lastBooking.getBookerId(),
              lastBooking.getStart(),
              lastBooking.getEnd()
      ));
    }

    if (nextBooking != null) {
      itemDto.setNextBooking(new BookingShortDto(
              nextBooking.getId(),
              nextBooking.getBookerId(),
              nextBooking.getStart(),
              nextBooking.getEnd()
      ));
    }
  }

  public List<ItemDto> getItemsByOwner(Long ownerId) {
    return itemRepository.findByOwnerId(ownerId).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
  }

  public List<ItemDto> searchItems(String text) {
    return itemRepository.search(text).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
  }

  public List<ItemDto> getItemsByRequestId(Long requestId) {
    return itemRepository.findAll().stream()
            .filter(item -> requestId.equals(item.getRequestId()))
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
  }
}
