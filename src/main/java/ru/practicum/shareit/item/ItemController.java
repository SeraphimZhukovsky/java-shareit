package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

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
  public ItemDto getItemById(@PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
    return itemService.getItemById(itemId, userId);
  }

  @GetMapping
  public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
    return itemService.getItemsByOwner(ownerId);
  }

  @GetMapping("/search")
  public List<ItemDto> searchItems(@RequestParam String text) {
    return itemService.searchItems(text);
  }
}
