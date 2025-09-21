package ru.practicum.shareit.request;

import jakarta.validation.ValidationException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
  private final ItemRequestService requestService;

  public ItemRequestController(ItemRequestService requestService) {
    this.requestService = requestService;
  }

  @PostMapping
  public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long requestorId) {
    if (requestorId == null) {
      throw new ValidationException("User ID header is required");
    }
    return requestService.createItemRequest(itemRequestDto, requestorId);
  }

  @GetMapping
  public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
    return requestService.getUserRequests(requestorId);
  }

  @GetMapping("/all")
  public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
    return requestService.getAllRequests(userId, from, size);
  }

  @GetMapping("/{requestId}")
  public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
    return requestService.getRequestById(requestId, userId);
  }
}
