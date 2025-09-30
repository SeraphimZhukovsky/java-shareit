package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
  private Long id;
  private String name;
  private String description;
  private Boolean available;
  private Long ownerId;


  public Item(Long id, String name, String description, Boolean available,
              Long ownerId, Long requestId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.available = available;
    this.ownerId = ownerId;
  }
}
