package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemRepository {
  private final Map<Long, Item> items = new HashMap<>();
  private Long nextId = 1L;

  public Item save(Item item) {
    item.setId(nextId++);
    items.put(item.getId(), item);
    return item;
  }

  public Optional<Item> findById(Long id) {
    return Optional.ofNullable(items.get(id));
  }

  public List<Item> findAll() {
    return items.values().stream().collect(Collectors.toList());
  }

  public Item update(Item item) {
    items.put(item.getId(), item);
    return item;
  }

  public List<Item> findByOwnerId(Long ownerId) {
    return items.values().stream()
            .filter(item -> item.getOwnerId().equals(ownerId))
            .collect(Collectors.toList());
  }

  public List<Item> search(String text) {
    if (text.isBlank()) {
      return List.of();
    }
    String searchText = text.toLowerCase();
    return items.values().stream()
            .filter(item -> item.getAvailable() &&
                    (item.getName().toLowerCase().contains(searchText) ||
                            item.getDescription().toLowerCase().contains(searchText)))
            .collect(Collectors.toList());
  }
}
