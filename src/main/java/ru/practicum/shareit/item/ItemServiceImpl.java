package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
  private final ItemRepository itemRepository;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final CommentRepository commentRepository;

  @Override
  @Transactional
  public ItemDto createItem(ItemDto itemDto, Long ownerId) {
    log.info("Creating item for owner ID: {}", ownerId);
    User owner = getUserById(ownerId);

    Item item = ItemMapper.toItem(itemDto, owner);
    Item savedItem = itemRepository.save(item);
    log.info("Item created with ID: {}", savedItem.getId());
    return ItemMapper.toItemDto(savedItem);
  }

  @Override
  @Transactional
  public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
    log.info("Updating item ID: {} by owner ID: {}", itemId, ownerId);

    Item existingItem = getItemById(itemId);

    // Проверка, что пользователь - владелец вещи
    if (!existingItem.getOwner().getId().equals(ownerId)) {
      log.warn("Access denied for user ID: {} to update item ID: {}", ownerId, itemId);
      throw new AccessDeniedException("Access denied");
    }

    // Обновление полей
    if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
      existingItem.setName(itemDto.getName());
    }
    if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
      existingItem.setDescription(itemDto.getDescription());
    }
    if (itemDto.getAvailable() != null) {
      existingItem.setAvailable(itemDto.getAvailable());
    }

    Item updatedItem = itemRepository.save(existingItem);
    log.info("Item ID: {} updated successfully", itemId);
    return ItemMapper.toItemDto(updatedItem);
  }

  @Override
  public ItemWithBookingsDto getItemById(Long itemId, Long userId) {
    log.info("Getting item ID: {} for user ID: {}", itemId, userId);

    Item item = getItemById(itemId);

    ItemWithBookingsDto itemWithBookings = ItemMapper.toItemWithBookingsDto(item);

    // Добавляем информацию о бронированиях только для владельца
    if (item.getOwner().getId().equals(userId)) {
      addBookingInfo(item, itemWithBookings);
    }

    // Добавляем комментарии
    addCommentsInfo(itemId, itemWithBookings);

    return itemWithBookings;
  }

  @Override
  public List<ItemWithBookingsDto> getItemsByOwner(Long ownerId) {
    log.info("Getting all items for owner ID: {}", ownerId);

    getUserById(ownerId);

    List<Item> items = itemRepository.findByOwnerId(ownerId);

    if (items.isEmpty()) {
      return List.of();
    }

    List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

    List<Booking> allBookings = bookingRepository.findApprovedBookingsForItems(itemIds);
    Map<Long, List<Booking>> bookingsByItemId = allBookings.stream()
            .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

    List<Comment> allComments = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds);
    Map<Long, List<Comment>> commentsByItemId = allComments.stream()
            .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

    return items.stream().map(item -> {
      ItemWithBookingsDto dto = ItemMapper.toItemWithBookingsDto(item);

      // Бронирования из мапы
      List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());
      addBookingInfoFromList(item, dto, itemBookings);

      // Комментарии из мапы
      List<Comment> itemComments = commentsByItemId.getOrDefault(item.getId(), List.of());
      List<CommentDto> commentDtos = itemComments.stream()
              .map(CommentMapper::toCommentDto)
              .collect(Collectors.toList());
      dto.setComments(commentDtos);

      return dto;
    }).collect(Collectors.toList());
  }

  private void addBookingInfoFromList(Item item, ItemWithBookingsDto dto, List<Booking> itemBookings) {
    LocalDateTime now = LocalDateTime.now();

        Optional<Booking> lastBooking = itemBookings.stream()
            .filter(booking -> booking.getStart().isBefore(now))
            .max(Comparator.comparing(Booking::getStart));

    if (lastBooking.isPresent()) {
      Booking booking = lastBooking.get();
      dto.setLastBooking(new ItemWithBookingsDto.Booking(
              booking.getId(),
              booking.getBooker().getId(),
              booking.getStart(),
              booking.getEnd()
      ));
    }

    Optional<Booking> nextBooking = itemBookings.stream()
            .filter(booking -> booking.getStart().isAfter(now))
            .min(Comparator.comparing(Booking::getStart));

    if (nextBooking.isPresent()) {
      Booking booking = nextBooking.get();
      dto.setNextBooking(new ItemWithBookingsDto.Booking(
              booking.getId(),
              booking.getBooker().getId(),
              booking.getStart(),
              booking.getEnd()
      ));
    }
  }

  private User getUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
  }

  @Override
  public List<ItemDto> searchItems(String text) {
    log.info("Searching items with text: {}", text);
    if (text == null || text.isBlank()) {
      return List.of();
    }
    return itemRepository.search(text).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public CommentDto addComment(Long itemId, CommentRequestDto commentRequestDto, Long authorId) {
    User author = getUserById(authorId);

    Item item = getItemById(itemId);

    // Проверяем, что пользователь брал вещь в аренду (любой статус кроме REJECTED)
    List<Booking> pastBookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(
            authorId, itemId, LocalDateTime.now());

    // Фильтруем - оставляем только не отклоненные бронирования
    pastBookings = pastBookings.stream()
            .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
            .collect(Collectors.toList());

    if (pastBookings.isEmpty()) {
      throw new ValidationException("User can only comment on items they have booked and used in the past");
    }

    Comment comment = new Comment();
    comment.setText(commentRequestDto.getText());
    comment.setItem(item);
    comment.setAuthor(author);
    comment.setCreated(LocalDateTime.now());

    Comment savedComment = commentRepository.save(comment);
    log.info("Comment added for item ID: {} by user ID: {}", itemId, authorId);

    return CommentMapper.toCommentDto(savedComment);
  }

  private Item getItemById(Long itemId) {
    return itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
  }

  private void addBookingInfo(Item item, ItemWithBookingsDto dto) {
    LocalDateTime now = LocalDateTime.now();

    // Последнее бронирование
    List<Booking> lastBookings = bookingRepository.findLastBookingForItem(item.getId(), now);
    if (!lastBookings.isEmpty()) {
      Booking lastBooking = lastBookings.get(0);
      dto.setLastBooking(new ItemWithBookingsDto.Booking(
              lastBooking.getId(),
              lastBooking.getBooker().getId(),
              lastBooking.getStart(),
              lastBooking.getEnd()
      ));
    }

    // Следующее бронирование
    List<Booking> nextBookings = bookingRepository.findNextBookingForItem(item.getId(), now);
    if (!nextBookings.isEmpty()) {
      Booking nextBooking = nextBookings.get(0);
      dto.setNextBooking(new ItemWithBookingsDto.Booking(
              nextBooking.getId(),
              nextBooking.getBooker().getId(),
              nextBooking.getStart(),
              nextBooking.getEnd()
      ));
    }
  }

  private void addCommentsInfo(Long itemId, ItemWithBookingsDto dto) {
    List<Comment> comments = commentRepository.findByItemId(itemId);
    List<CommentDto> commentDtos = comments.stream()
            .map(CommentMapper::toCommentDto)
            .collect(Collectors.toList());
    dto.setComments(commentDtos);
  }
}