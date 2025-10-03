package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
  ItemDto createItem(ItemDto itemDto, Long ownerId);

  ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId);

  ItemWithBookingsDto getItemById(Long itemId, Long userId);

  List<ItemWithBookingsDto> getItemsByOwner(Long ownerId);

  List<ItemDto> searchItems(String text);

  CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId);
}
