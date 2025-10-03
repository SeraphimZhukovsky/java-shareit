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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
    userRepository.findById(ownerId)
            .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

    Item item = ItemMapper.toItem(itemDto, ownerId);
    Item savedItem = itemRepository.save(item);
    log.info("Item created with ID: {}", savedItem.getId());
    return ItemMapper.toItemDto(savedItem);
  }

  @Override
  @Transactional
  public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
    log.info("Updating item ID: {} by owner ID: {}", itemId, ownerId);

    Item existingItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

    if (!existingItem.getOwnerId().equals(ownerId)) {
      log.warn("Access denied for user ID: {} to update item ID: {}", ownerId, itemId);
      throw new AccessDeniedException("Access denied");
    }

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

    Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

    ItemWithBookingsDto itemWithBookings = ItemMapper.toItemWithBookingsDto(item);

    // Добавляем информацию о бронированиях только для владельца
    if (item.getOwnerId().equals(userId)) {
      addBookingInfo(item, itemWithBookings);
    }

    // Добавляем комментарии
    addCommentsInfo(itemId, itemWithBookings);

    return itemWithBookings;
  }

  @Override
  public List<ItemWithBookingsDto> getItemsByOwner(Long ownerId) {
    log.info("Getting all items for owner ID: {}", ownerId);

    userRepository.findById(ownerId)
            .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

    List<Item> items = itemRepository.findByOwnerId(ownerId);
    List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

    // Получаем все комментарии для этих items
    List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);

    return items.stream().map(item -> {
      ItemWithBookingsDto dto = ItemMapper.toItemWithBookingsDto(item);
      addBookingInfo(item, dto);

      // Добавляем комментарии для текущего item
      List<CommentDto> itemComments = allComments.stream()
              .filter(comment -> comment.getItem().getId().equals(item.getId()))
              .map(this::toCommentDto)
              .collect(Collectors.toList());
      dto.setComments(itemComments);

      return dto;
    }).collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> searchItems(String text) {
    log.info("Searching items with text: {}", text);
    if (text.isBlank()) {
      return List.of();
    }
    return itemRepository.search(text).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
    User author = userRepository.findById(authorId)
            .orElseThrow(() -> new NotFoundException("User with id " + authorId + " not found"));

    Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

    // Проверяем, что пользователь действительно брал вещь в аренду
    List<Booking> pastBookings = bookingRepository.findByBookerIdAndItemIdAndEndBeforeAndStatus(
            authorId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

    if (pastBookings.isEmpty()) {
      throw new ValidationException("User can only comment on items they have booked and used in the past");
    }

    Comment comment = new Comment();
    comment.setText(commentDto.getText());
    comment.setItem(item);
    comment.setAuthor(author);
    comment.setCreated(LocalDateTime.now());

    Comment savedComment = commentRepository.save(comment);
    log.info("Comment added for item ID: {} by user ID: {}", itemId, authorId);

    return toCommentDto(savedComment);
  }

  private void addBookingInfo(Item item, ItemWithBookingsDto dto) {
    LocalDateTime now = LocalDateTime.now();

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
            .map(this::toCommentDto)
            .collect(Collectors.toList());
    dto.setComments(commentDtos);
  }

  private CommentDto toCommentDto(Comment comment) {
    return new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getAuthor().getName(),
            comment.getCreated()
    );
  }
}