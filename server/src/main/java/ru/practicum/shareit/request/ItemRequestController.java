package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
  private final ItemRequestService itemRequestService;
  private static final String USER_ID_HEADER = "X-Sharer-User-Id";

  @PostMapping
  public ItemRequestResponseDto createRequest(
          @RequestBody ItemRequestDto itemRequestDto,
          @RequestHeader(USER_ID_HEADER) Long requesterId) {
    log.info("Creating item request for user ID: {}", requesterId);
    return itemRequestService.createRequest(itemRequestDto, requesterId);
  }

  @GetMapping
  public List<ItemRequestResponseDto> getUserRequests(
          @RequestHeader(USER_ID_HEADER) Long requesterId) {
    log.info("Getting item requests for user ID: {}", requesterId);
    return itemRequestService.getUserRequests(requesterId);
  }

  @GetMapping("/all")
  public List<ItemRequestResponseDto> getOtherUsersRequests(
          @RequestHeader(USER_ID_HEADER) Long userId,
          @RequestParam(defaultValue = "0") Integer from,
          @RequestParam(defaultValue = "10") Integer size) {
    log.info("Getting other users requests for user ID: {}, from: {}, size: {}", userId, from, size);
    return itemRequestService.getOtherUsersRequests(userId, from, size);
  }

  @GetMapping("/{requestId}")
  public ItemRequestResponseDto getRequestById(
          @PathVariable Long requestId,
          @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Getting item request ID: {} for user ID: {}", requestId, userId);
    return itemRequestService.getRequestById(requestId, userId);
  }
}