package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    private final BookingDto.Booker booker = new BookingDto.Booker(2L, "Booker");
    private final BookingDto.Item item = new BookingDto.Item(1L, "Item");
    private final BookingDto bookingDto = new BookingDto(1L, start, end, BookingStatus.APPROVED, booker, item);

    @Test
    void createBookingTest() throws Exception {
        BookingRequestDto createDto = new BookingRequestDto(1L, start, end);

        Mockito.when(bookingService.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void approveBookingTest() throws Exception {
        Mockito.when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito.when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserBookingsTest() throws Exception {
        Mockito.when(bookingService.getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOwnerBookingsTest() throws Exception {
        Mockito.when(bookingService.getOwnerBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}