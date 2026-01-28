package com.example.stock.controller;

import com.example.stock.model.Inventory;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventory sampleInventory;

    @BeforeEach
    void setUp() {
        sampleInventory = new Inventory();
        sampleInventory.setId(1L);
        sampleInventory.setItemId(1L);
        sampleInventory.setQty(50);
        sampleInventory.setType("T");
    }

    @Test
    void testGetAllInventory() throws Exception {
        Page<Inventory> inventoryPage = new PageImpl<>(Collections.singletonList(sampleInventory));
        when(stockService.getAllInventory(any(Pageable.class))).thenReturn(inventoryPage);

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("T"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testAddInventory() throws Exception {
        when(stockService.addInventory(any(Inventory.class))).thenReturn(sampleInventory);

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleInventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("T"));
    }

    @Test
    void testUpdateInventory() throws Exception {
        when(stockService.updateInventory(eq(1L), any(Inventory.class))).thenReturn(sampleInventory);

        mockMvc.perform(put("/api/inventory/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleInventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteInventory() throws Exception {
        mockMvc.perform(delete("/api/inventory/{id}", 1L))
                .andExpect(status().isOk());

        verify(stockService).deleteInventory(1L);
    }
}
