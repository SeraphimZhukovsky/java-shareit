package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
  private Long id;
  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotBlank(message = "Description cannot be blank")
  private String description;

  @NotNull(message = "Available status cannot be null")
  private Boolean available;

  private Long requestId;

  private BookingShortDto lastBooking;
  private BookingShortDto nextBooking;

  public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.available = available;
    this.requestId = requestId;
    this.lastBooking = null;
    this.nextBooking = null;
  }
}
