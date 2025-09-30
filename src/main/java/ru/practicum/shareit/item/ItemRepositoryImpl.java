package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
  private final Map<Long, Item> items = new HashMap<>();
  private Long nextId = 1L;

  @Override
  public Item save(Item item) {
    item.setId(nextId++);
    items.put(item.getId(), item);
    return item;
  }

  @Override
  public Optional<Item> findById(Long id) {
    return Optional.ofNullable(items.get(id));
  }

  @Override
  public List<Item> findAll() {
    return items.values().stream().collect(Collectors.toList());
  }

  @Override
  public Item update(Item item) {
    items.put(item.getId(), item);
    return item;
  }

  @Override
  public List<Item> findByOwnerId(Long ownerId) {
    return items.values().stream()
            .filter(item -> item.getOwnerId().equals(ownerId))
            .collect(Collectors.toList());
  }

  @Override
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