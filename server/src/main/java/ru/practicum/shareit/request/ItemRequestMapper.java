package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {

  public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
    ItemRequest itemRequest = new ItemRequest();
    itemRequest.setDescription(itemRequestDto.getDescription());
    itemRequest.setRequester(requester);
    return itemRequest;
  }

  public static ItemRequestResponseDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
    return new ItemRequestResponseDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            items
    );
  }
}