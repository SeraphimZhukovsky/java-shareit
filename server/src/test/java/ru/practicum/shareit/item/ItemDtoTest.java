package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

  @Autowired
  private JacksonTester<ItemWithBookingsDto> json;

  @Test
  void testSerialize() throws Exception {
    User owner = new User();
    owner.setId(1L);
    owner.setName("John");
    owner.setEmail("john@example.com");

    ItemWithBookingsDto.Booking lastBooking = new ItemWithBookingsDto.Booking(
            100L, 2L, LocalDateTime.of(2024, 6, 2, 0, 0, 0), LocalDateTime.of(2024, 6, 4, 0, 0, 0)
    );

    ItemWithBookingsDto.Booking nextBooking = new ItemWithBookingsDto.Booking(
            101L, 3L, LocalDateTime.of(2024, 6, 12, 0, 0, 0), LocalDateTime.of(2024, 6, 17, 0, 0, 0)
    );

    ItemWithBookingsDto itemDto = new ItemWithBookingsDto(
            1L, "new_item", "new_description", true, null, lastBooking, nextBooking, List.of()
    );

    assertThat(json.write(itemDto)).hasJsonPathNumberValue("$.id");
    assertThat(json.write(itemDto)).hasJsonPathStringValue("$.name");
    assertThat(json.write(itemDto)).hasJsonPathStringValue("$.description");
    assertThat(json.write(itemDto)).hasJsonPathBooleanValue("$.available");
    assertThat(json.write(itemDto)).hasJsonPathMapValue("$.lastBooking");
    assertThat(json.write(itemDto)).hasJsonPathMapValue("$.nextBooking");
    assertThat(json.write(itemDto)).hasJsonPathArrayValue("$.comments");
  }

  @Test
  void testDeserialize() throws Exception {
    String content = "{" +
            "\"id\": 1," +
            "\"name\": \"new_item\"," +
            "\"description\": \"new_description\"," +
            "\"available\": true," +
            "\"requestId\": null," +
            "\"lastBooking\": {" +
            "  \"id\": 100," +
            "  \"bookerId\": 2," +
            "  \"start\": \"2024-06-02T00:00:00\"," +
            "  \"end\": \"2024-06-04T00:00:00\"" +
            "}," +
            "\"nextBooking\": {" +
            "  \"id\": 101," +
            "  \"bookerId\": 3," +
            "  \"start\": \"2024-06-12T00:00:00\"," +
            "  \"end\": \"2024-06-17T00:00:00\"" +
            "}," +
            "\"comments\": []" +
            "}";

    ItemWithBookingsDto itemDto = json.parseObject(content);

    assertThat(itemDto.getId()).isEqualTo(1L);
    assertThat(itemDto.getName()).isEqualTo("new_item");
    assertThat(itemDto.getDescription()).isEqualTo("new_description");
    assertThat(itemDto.getLastBooking().getId()).isEqualTo(100L);
    assertThat(itemDto.getNextBooking().getId()).isEqualTo(101L);
    assertThat(itemDto.getComments()).isEmpty();
  }
}