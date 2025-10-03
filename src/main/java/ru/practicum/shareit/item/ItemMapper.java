package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;

public class ItemMapper {
  public static ItemDto toItemDto(Item item) {
    return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable()
    );
  }

  public static Item toItem(ItemDto itemDto, Long ownerId) {
    return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.getAvailable(),
            ownerId
    );
  }

  public static ItemWithBookingsDto toItemWithBookingsDto(Item item) {
    return new ItemWithBookingsDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            null, // lastBooking
            null, // nextBooking
            Collections.emptyList() // comments
    );
  }
}
