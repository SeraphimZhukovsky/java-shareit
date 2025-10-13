package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
  private final ItemClient itemClient;
  private final BookingClient bookingClient;

  @PostMapping
  public ResponseEntity<Object> createItem(
          @Valid @RequestBody ItemDto itemDto,
          @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
    log.info("Creating item for owner ID: {}", ownerId);
    return itemClient.createItem(itemDto, ownerId);
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<Object> updateItem(
          @PathVariable Long itemId,
          @RequestBody ItemDto itemDto,
          @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
    log.info("Updating item ID: {} by owner ID: {}", itemId, ownerId);
    return itemClient.updateItem(itemId, itemDto, ownerId);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<Object> getItemById(
          @PathVariable Long itemId,
          @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
    log.info("Getting item ID: {} for user ID: {}", itemId, userId);
    return itemClient.getItemById(itemId, userId);
  }

  @GetMapping
  public ResponseEntity<Object> getItemsByOwner(
          @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
    log.info("Getting all items for owner ID: {}", ownerId);
    return itemClient.getItemsByOwner(ownerId);
  }

  @GetMapping("/search")
  public ResponseEntity<Object> searchItems(
          @RequestParam @NotNull String text,
          @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {

    if (text == null || text.isBlank()) {
      return ResponseEntity.ok(List.of());
    }

    log.info("Searching items with text: {}", text);
    return itemClient.searchItems(text, userId);
  }

  @PostMapping("/{itemId}/comment")
  public ResponseEntity<Object> addComment(
          @PathVariable Long itemId,
          @Valid @RequestBody CommentRequestDto commentRequestDto,
          @RequestHeader("X-Sharer-User-Id") @NotNull Long authorId) {

    log.info("Adding comment for item ID: {} by user ID: {}", itemId, authorId);
    return itemClient.addComment(itemId, commentRequestDto, authorId);
  }
}