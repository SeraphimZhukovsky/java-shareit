package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "is_available", nullable = false)
  private Boolean available;

  @Column(name = "owner_id", nullable = false)
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
