package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
  private final ItemRepository itemRepository = new ItemRepository();
  private final UserService userService;

  public ItemService(UserService userService) {
    this.userService = userService;
  }

  public ItemDto createItem(ItemDto itemDto, Long ownerId) {
    userService.getUserById(ownerId);

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

    return ItemMapper.toItemDto(item);
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
}
