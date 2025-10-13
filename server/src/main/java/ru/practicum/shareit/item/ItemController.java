package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
  private final ItemService itemService;

  @PostMapping
  public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
    if (ownerId == null) {
      throw new ValidationException("User ID header is required");
    }
    return itemService.createItem(itemDto, ownerId);
  }

  @PatchMapping("/{itemId}")
  public ItemDto updateItem(@PathVariable Long itemId,
                            @RequestBody ItemDto itemDto, // УБРАТЬ @Valid
                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
    return itemService.updateItem(itemId, itemDto, ownerId);
  }

  @GetMapping("/{itemId}")
  public ItemWithBookingsDto getItemById(@PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
    return itemService.getItemById(itemId, userId);
  }

  @GetMapping
  public List<ItemWithBookingsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
    return itemService.getItemsByOwner(ownerId);
  }

  @GetMapping("/search")
  public List<ItemDto> searchItems(@RequestParam String text) {
    return itemService.searchItems(text);
  }

  @PostMapping("/{itemId}/comment")
  public CommentDto addComment(@PathVariable Long itemId,
                               @Valid @RequestBody CommentRequestDto commentRequestDto,
                               @RequestHeader("X-Sharer-User-Id") Long authorId) {
    return itemService.addComment(itemId, commentRequestDto, authorId);
  }
}
