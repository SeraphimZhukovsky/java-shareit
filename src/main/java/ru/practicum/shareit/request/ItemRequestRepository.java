package ru.practicum.shareit.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemRequestRepository {
  private final Map<Long, ItemRequest> requests = new HashMap<>();
  private Long nextId = 1L;

  public ItemRequest save(ItemRequest itemRequest) {
    itemRequest.setId(nextId++);
    requests.put(itemRequest.getId(), itemRequest);
    return itemRequest;
  }

  public Optional<ItemRequest> findById(Long id) {
    return Optional.ofNullable(requests.get(id));
  }

  public List<ItemRequest> findByRequestorId(Long requestorId) {
    return requests.values().stream()
            .filter(request -> request.getRequestorId().equals(requestorId))
            .collect(Collectors.toList());
  }

  public List<ItemRequest> findAll() {
    return requests.values().stream().collect(Collectors.toList());
  }

  public List<ItemRequest> findAllExceptRequestor(Long requestorId, int from, int size) {
    return requests.values().stream()
            .filter(request -> !request.getRequestorId().equals(requestorId))
            .skip(from)
            .limit(size)
            .collect(Collectors.toList());
  }
}