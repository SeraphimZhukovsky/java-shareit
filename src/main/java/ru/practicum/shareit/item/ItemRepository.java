package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
  Item save(Item item);

  Optional<Item> findById(Long id);

  List<Item> findAll();

  Item update(Item item);

  List<Item> findByOwnerId(Long ownerId);
}
