package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
  @NotNull
  private Long id;

  @NotNull
  private String text;

  @NotNull
  private String authorName;

  @NotNull
  private LocalDateTime created;
}
