package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldSerializeUserDto() throws Exception {
    UserDto userDto = new UserDto(1L, "John", "john@email.com");

    String json = objectMapper.writeValueAsString(userDto);

    assertThat(json).contains("\"id\":1");
    assertThat(json).contains("\"name\":\"John\"");
    assertThat(json).contains("\"email\":\"john@email.com\"");
  }

  @Test
  void shouldDeserializeUserDto() throws Exception {
    String json = "{\"id\":1,\"name\":\"John\",\"email\":\"john@email.com\"}";

    UserDto userDto = objectMapper.readValue(json, UserDto.class);

    assertThat(userDto.getId()).isEqualTo(1L);
    assertThat(userDto.getName()).isEqualTo("John");
    assertThat(userDto.getEmail()).isEqualTo("john@email.com");
  }
}