package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ItemRequestService itemRequestService;

  @Autowired
  private ObjectMapper objectMapper;

  private final Long userId = 1L;
  private final Long requestId = 2L;

  @Test
  void createRequestTest() throws Exception {
    ItemRequestDto createDto = new ItemRequestDto(null, "Need a drill", null);
    ItemRequestResponseDto responseDto = new ItemRequestResponseDto(requestId, "Need a drill",
            LocalDateTime.now(), List.of());

    Mockito.when(itemRequestService.createRequest(any(ItemRequestDto.class), eq(userId)))
            .thenReturn(responseDto);

    mockMvc.perform(post("/requests")
                    .header("X-Sharer-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(requestId))
            .andExpect(jsonPath("$.description").value("Need a drill"));
  }

  @Test
  void getUserRequestsTest() throws Exception {
    ItemRequestResponseDto dto = new ItemRequestResponseDto(requestId, "Need a hammer",
            LocalDateTime.now(), List.of());

    Mockito.when(itemRequestService.getUserRequests(userId))
            .thenReturn(List.of(dto));

    mockMvc.perform(get("/requests")
                    .header("X-Sharer-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(requestId))
            .andExpect(jsonPath("$[0].description").value("Need a hammer"));
  }

  @Test
  void getOtherUsersRequestsTest() throws Exception {
    ItemRequestResponseDto dto = new ItemRequestResponseDto(requestId, "Need a ladder",
            LocalDateTime.now(), List.of());

    Mockito.when(itemRequestService.getOtherUsersRequests(anyLong(), anyInt(), anyInt()))
            .thenReturn(List.of(dto));

    mockMvc.perform(get("/requests/all")
                    .header("X-Sharer-User-Id", userId)
                    .param("from", "0")
                    .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(requestId))
            .andExpect(jsonPath("$[0].description").value("Need a ladder"));
  }

  @Test
  void getRequestByIdTest() throws Exception {
    ItemRequestResponseDto dto = new ItemRequestResponseDto(requestId, "Need a chainsaw",
            LocalDateTime.now(), List.of());

    Mockito.when(itemRequestService.getRequestById(requestId, userId))
            .thenReturn(dto);

    mockMvc.perform(get("/requests/{requestId}", requestId)
                    .header("X-Sharer-User-Id", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(requestId))
            .andExpect(jsonPath("$.description").value("Need a chainsaw"));
  }
}