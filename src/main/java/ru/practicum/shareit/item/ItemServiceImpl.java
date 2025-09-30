package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
  private final ItemRepository itemRepository;
  private final UserServiceImpl userService;

  public ItemServiceImpl(ItemRepository itemRepository, UserServiceImpl userService) {
    this.itemRepository = itemRepository;
    this.userService = userService;
  }

  @Override
  public ItemDto createItem(ItemDto itemDto, Long ownerId) {
    log.info("Creating item for owner ID: {}", ownerId);
    userService.getUserById(ownerId);

    Item item = ItemMapper.toItem(itemDto, ownerId);
    Item savedItem = itemRepository.save(item);
    log.info("Item created with ID: {}", savedItem.getId());
    return ItemMapper.toItemDto(savedItem);
  }

  @Override
  public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
    log.info("Updating item ID: {} by owner ID: {}", itemId, ownerId);

    Item existingItem = getItemByIdOrThrow(itemId);

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

    Item updatedItem = itemRepository.update(existingItem);
    log.info("Item ID: {} updated successfully", itemId);
    return ItemMapper.toItemDto(updatedItem);
  }

  @Override
  public ItemDto getItemById(Long itemId, Long userId) {
    log.info("Getting item ID: {} for user ID: {}", itemId, userId);

    Item item = getItemByIdOrThrow(itemId);

    return ItemMapper.toItemDto(item);
  }

  @Override
  public List<ItemDto> getItemsByOwner(Long ownerId) {
    log.info("Getting all items for owner ID: {}", ownerId);

    userService.getUserById(ownerId);
    return itemRepository.findByOwnerId(ownerId).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> searchItems(String text) {
    log.info("Searching items with text: {}", text);

    if (text.isBlank()) {
      return List.of();
    }

    String searchText = text.toLowerCase();
    List<Item> allItems = itemRepository.findAll();

    List<ItemDto> result = itemRepository.search(text).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());

    log.info("Found {} items for search text: {}", result.size(), text);
    return result;
  }

  private Item getItemByIdOrThrow(Long itemId) {
    return itemRepository.findById(itemId)
            .orElseThrow(() -> {
              log.warn("Item not found with ID: {}", itemId);
              return new NotFoundException("Item with id " + itemId + " not found");
            });
  }
}