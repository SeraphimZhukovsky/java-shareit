package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
  ItemRequestResponseDto createRequest(ItemRequestDto itemRequestDto, Long requesterId);

  List<ItemRequestResponseDto> getUserRequests(Long requesterId);

  List<ItemRequestResponseDto> getOtherUsersRequests(Long userId, Integer from, Integer size);

  ItemRequestResponseDto getRequestById(Long requestId, Long userId);
}