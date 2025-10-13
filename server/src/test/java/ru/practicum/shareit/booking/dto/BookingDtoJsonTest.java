package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingDto() throws Exception {
        BookingDto.Booker booker = new BookingDto.Booker(201L, "Tom");
        BookingDto.Item item = new BookingDto.Item(100L, "Drill");
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0),
                BookingStatus.APPROVED, booker, item);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2024-01-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2024-01-02T10:00:00\"");
        assertThat(json).contains("\"status\":\"APPROVED\"");
    }

    @Test
    void shouldDeserializeBookingDto() throws Exception {
        String json = "{\"id\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"," +
                "\"status\":\"APPROVED\",\"booker\":{\"id\":201,\"name\":\"Tom\"}," +
                "\"item\":{\"id\":100,\"name\":\"Drill\"}}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingDto.getBooker().getId()).isEqualTo(201L);
        assertThat(bookingDto.getItem().getId()).isEqualTo(100L);
    }
}