package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingsDto {
  private Long id;
  private String name;
  private String description;
  private Boolean available;
  private Long requestId;
  private Booking lastBooking;
  private Booking nextBooking;
  private List<CommentDto> comments;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Booking {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
  }
}