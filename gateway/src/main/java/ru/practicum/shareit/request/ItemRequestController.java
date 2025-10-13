package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
  private final ItemRequestClient itemRequestClient;

  @PostMapping
  public ResponseEntity<Object> createRequest(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @Valid @RequestBody ItemRequestDto requestDto) {
    log.info("Creating item request for user ID: {}", userId);
    return itemRequestClient.createRequest(requestDto, userId);
  }

  @GetMapping
  public ResponseEntity<Object> getUserRequests(
          @RequestHeader("X-Sharer-User-Id") Long userId) {
    log.info("Getting item requests for user ID: {}", userId);
    return itemRequestClient.getUserRequests(userId);
  }

  @GetMapping("/all")
  public ResponseEntity<Object> getOtherUsersRequests(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
    log.info("Getting other users requests for user ID: {}, from: {}, size: {}", userId, from, size);
    return itemRequestClient.getOtherUsersRequests(userId, from, size);
  }

  @GetMapping("/{requestId}")
  public ResponseEntity<Object> getRequestById(
          @RequestHeader("X-Sharer-User-Id") Long userId,
          @PathVariable Long requestId) {
    log.info("Getting item request ID: {} for user ID: {}", requestId, userId);
    return itemRequestClient.getRequestById(requestId, userId);
  }
}