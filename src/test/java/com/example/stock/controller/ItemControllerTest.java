package com.example.stock.controller;

import com.example.stock.model.Item;
import com.example.stock.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setName("Test Item");
        sampleItem.setPrice(10.0);
        sampleItem.setStock(100);
    }

    @Test
    void testGetAllItems() throws Exception {
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(sampleItem));
        when(stockService.getAllItems(any(Pageable.class))).thenReturn(itemPage);

        mockMvc.perform(get("/api/items")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Item"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetItem_Found() throws Exception {
        when(stockService.getItem(1L)).thenReturn(Optional.of(sampleItem));

        mockMvc.perform(get("/api/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void testGetItem_NotFound() throws Exception {
        when(stockService.getItem(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateItem() throws Exception {
        when(stockService.saveItem(any(Item.class))).thenReturn(sampleItem);

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateItem() throws Exception {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Name");
        updatedItem.setPrice(20.0);

        when(stockService.updateItem(eq(1L), any(Item.class))).thenReturn(updatedItem);

        mockMvc.perform(put("/api/items/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/items/{id}", 1L))
                .andExpect(status().isOk());

        verify(stockService).deleteItem(1L);
    }
}
