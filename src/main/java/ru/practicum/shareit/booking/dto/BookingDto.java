package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
  @NotNull
  private Long id;

  @NotNull
  private LocalDateTime start;

  @NotNull
  private LocalDateTime end;

  @NotNull
  private BookingStatus status;

  @NotNull
  private Booker booker;

  @NotNull
  private Item item;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Booker {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Item {
    private Long id;
    private String name;
  }
}