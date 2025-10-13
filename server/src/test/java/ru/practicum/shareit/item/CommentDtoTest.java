package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

  @Autowired
  private JacksonTester<CommentDto> json;

  @Test
  void testSerialize() throws Exception {
    CommentDto dto = new CommentDto(10L, "text_test", "Alice", LocalDateTime.of(2024, 1, 1, 10, 0));

    assertThat(json.write(dto))
            .extractingJsonPathNumberValue("$.id").isEqualTo(10);
    assertThat(json.write(dto))
            .extractingJsonPathStringValue("$.text").isEqualTo("text_test");
    assertThat(json.write(dto))
            .extractingJsonPathStringValue("$.authorName").isEqualTo("Alice");
    assertThat(json.write(dto))
            .extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
  }

  @Test
  void testDeserialize() throws Exception {
    String content = "{" +
            "\"id\": 10," +
            "\"text\": \"text_test\"," +
            "\"authorName\": \"Bob\"," +
            "\"created\": \"2024-01-01T10:00:00\"" +
            "}";

    CommentDto dto = json.parseObject(content);

    assertThat(dto.getId()).isEqualTo(10);
    assertThat(dto.getText()).isEqualTo("text_test");
    assertThat(dto.getAuthorName()).isEqualTo("Bob");
    assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
  }
}