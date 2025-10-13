package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
  @NotNull(message = "ItemId cannot be null")
  private long itemId;

  @NotNull(message = "Start date cannot be null")
  @FutureOrPresent(message = "Start date cannot be in the past")
  private LocalDateTime start;

  @NotNull(message = "End date cannot be null")
  @Future(message = "End date must be in the future")
  private LocalDateTime end;
}