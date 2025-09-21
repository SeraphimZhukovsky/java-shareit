package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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
  private Long id;

  @NotNull(message = "Start date cannot be null")
  @FutureOrPresent(message = "Start date must be in present or future")
  private LocalDateTime start;

  @NotNull(message = "End date cannot be null")
  @Future(message = "End date must be in future")
  private LocalDateTime end;

  @NotNull(message = "Item ID cannot be null")
  private Long itemId;

  private Long bookerId;
  private BookingStatus status;
}
