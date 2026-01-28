package com.example.stock.controller;

import com.example.stock.model.Order;
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

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new Order();
        sampleOrder.setId(1L);
        sampleOrder.setOrderNo("ORD-001");
        sampleOrder.setItemId(1L);
        sampleOrder.setQty(5);
        sampleOrder.setPrice(50.0);
    }

    @Test
    void testGetAllOrders() throws Exception {
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(sampleOrder));
        when(stockService.getAllOrders(any(Pageable.class))).thenReturn(orderPage);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNo").value("ORD-001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testPlaceOrder() throws Exception {
        when(stockService.placeOrder(any(Order.class))).thenReturn(sampleOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNo").value("ORD-001"));
    }

    @Test
    void testUpdateOrder() throws Exception {
        when(stockService.updateOrder(eq(1L), any(Order.class))).thenReturn(sampleOrder);

        mockMvc.perform(put("/api/orders/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNo").value("ORD-001"));
    }

    @Test
    void testDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", 1L))
                .andExpect(status().isOk());

        verify(stockService).deleteOrder(1L);
    }
}
