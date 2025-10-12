package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

public class ItemMapper {

  public static ItemDto toItemDto(Item item) {
    ItemDto itemDto = new ItemDto();
    itemDto.setId(item.getId());
    itemDto.setName(item.getName());
    itemDto.setDescription(item.getDescription());
    itemDto.setAvailable(item.getAvailable());
    itemDto.setRequestId(null);
    itemDto.setRequestId(item.getRequestId());
    return itemDto;
  }

  public static Item toItem(ItemDto itemDto, User owner) {
    Item item = new Item();
    item.setId(itemDto.getId());
    item.setName(itemDto.getName());
    item.setDescription(itemDto.getDescription());
    item.setAvailable(itemDto.getAvailable());
    item.setOwner(owner);
    item.setRequestId(itemDto.getRequestId());
    return item;
  }

  public static ItemWithBookingsDto toItemWithBookingsDto(Item item) {
    ItemWithBookingsDto dto = new ItemWithBookingsDto();
    dto.setId(item.getId());
    dto.setName(item.getName());
    dto.setDescription(item.getDescription());
    dto.setAvailable(item.getAvailable());
    dto.setRequestId(null);
    dto.setLastBooking(null);
    dto.setNextBooking(null);
    dto.setComments(Collections.emptyList());
    return dto;
  }
}