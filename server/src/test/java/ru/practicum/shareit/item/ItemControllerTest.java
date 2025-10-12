package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long itemId = 1L;

    @Test
    void getItemsByOwnerTest() throws Exception {
        ItemWithBookingsDto item = new ItemWithBookingsDto(itemId, "Drill", "Description", true, null, null, null, List.of());

        Mockito.when(itemService.getItemsByOwner(userId)).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void getItemByIdTest() throws Exception {
        ItemWithBookingsDto item = new ItemWithBookingsDto(itemId, "Drill", "Description", true, null, null, null, List.of());

        Mockito.when(itemService.getItemById(itemId, userId)).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void createItemTest() throws Exception {
        ItemDto createDto = new ItemDto(null, "Drill", "Description", true, null);
        ItemDto responseDto = new ItemDto(itemId, "Drill", "Description", true, null);

        Mockito.when(itemService.createItem(any(ItemDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updateDto = new ItemDto(null, "Updated Drill", "Updated Description", true, null);
        ItemDto responseDto = new ItemDto(itemId, "Updated Drill", "Updated Description", true, null);

        Mockito.when(itemService.updateItem(anyLong(), any(ItemDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Drill"));
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDto item = new ItemDto(itemId, "Drill", "Description", true, null);

        Mockito.when(itemService.searchItems("drill")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentRequestDto createDto = new CommentRequestDto("Nice item!");
        CommentDto commentDto = new CommentDto(1L, "Nice item!", "Author", null);

        Mockito.when(itemService.addComment(anyLong(), any(CommentRequestDto.class), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Nice item!"));
    }
}