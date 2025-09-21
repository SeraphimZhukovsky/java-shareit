package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestService {
  private final ItemRequestRepository requestRepository = new ItemRequestRepository();
  private final UserService userService;

  public ItemRequestService(UserService userService) {
    this.userService = userService;
  }

  public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
    userService.getUserById(requestorId); // Проверяем существование пользователя

    ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
    itemRequest.setRequestorId(requestorId);
    itemRequest.setCreated(LocalDateTime.now());

    ItemRequest savedRequest = requestRepository.save(itemRequest);
    return ItemRequestMapper.toItemRequestDto(savedRequest);
  }

  public List<ItemRequestDto> getUserRequests(Long requestorId) {
    userService.getUserById(requestorId); // Проверяем существование пользователя

    return requestRepository.findByRequestorId(requestorId).stream()
            .map(ItemRequestMapper::toItemRequestDto)
            .collect(Collectors.toList());
  }

  public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
    userService.getUserById(userId);

    List<ItemRequest> allRequests = requestRepository.findAllExceptRequestor(userId, from, size);

    // Реализация пагинации
    int start = Math.min(from, allRequests.size());
    int end = Math.min(start + size, allRequests.size());

    return allRequests.stream()
            .skip(start)
            .limit(size)
            .map(ItemRequestMapper::toItemRequestDto)
            .collect(Collectors.toList());
  }

  public ItemRequestDto getRequestById(Long requestId, Long userId) {
    userService.getUserById(userId); // Проверяем существование пользователя

    ItemRequest itemRequest = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

    return ItemRequestMapper.toItemRequestDto(itemRequest);
  }
}