package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

  public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
    ItemRequest itemRequest = new ItemRequest();
    itemRequest.setDescription(itemRequestDto.getDescription());
    itemRequest.setRequester(requester);
    return itemRequest;
  }

  public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
    return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated()
    );
  }
}