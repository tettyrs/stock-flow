package com.example.stock.service;

import com.example.stock.exception.InsufficientStockException;
import com.example.stock.exception.ResourceNotFoundException;
import com.example.stock.model.Inventory;
import com.example.stock.model.Item;
import com.example.stock.model.Order;
import com.example.stock.repository.InventoryRepository;
import com.example.stock.repository.ItemRepository;
import com.example.stock.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private StockService stockService;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setName("Test Item");
        sampleItem.setPrice(10.0);
        sampleItem.setStock(100);
    }

    // --- ITEM TESTS ---

    @Test
    void testGetAllItems() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(sampleItem));
        when(itemRepository.findAll(pageable)).thenReturn(itemPage);

        Page<Item> result = stockService.getAllItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(itemRepository).findAll(pageable);
    }

    @Test
    void testGetItem_Found() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        Optional<Item> result = stockService.getItem(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Item", result.get().getName());
    }

    @Test
    void testGetItem_NotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Item> result = stockService.getItem(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);

        Item result = stockService.saveItem(sampleItem);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository).save(sampleItem);
    }

    @Test
    void testUpdateItem_Success() {
        Item updateDetails = new Item();
        updateDetails.setName("Updated Name");
        updateDetails.setPrice(20.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        stockService.updateItem(1L, updateDetails); // The result variable assignment is removed

        assertEquals("Updated Name", sampleItem.getName()); // Assertions changed to use sampleItem directly
        assertEquals(20.0, sampleItem.getPrice());
        assertEquals(100, sampleItem.getStock()); // Stock should not change
    }

    @Test
    void testUpdateItem_NotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockService.updateItem(99L, new Item()));
    }

    @Test
    void testDeleteItem() {
        doNothing().when(itemRepository).deleteById(1L);

        stockService.deleteItem(1L);

        verify(itemRepository).deleteById(1L);
    }

    // --- INVENTORY TESTS ---

    @Test
    void testAddInventory_TopUp() {
        Inventory inventory = new Inventory();
        inventory.setItemId(1L);
        inventory.setQty(50);
        inventory.setType("T");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory result = stockService.addInventory(inventory);

        assertNotNull(result);
        assertEquals(150, sampleItem.getStock()); // 100 + 50
        verify(itemRepository).save(sampleItem);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testAddInventory_Withdrawal_Success() {
        Inventory inventory = new Inventory();
        inventory.setItemId(1L);
        inventory.setQty(50);
        inventory.setType("W");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        stockService.addInventory(inventory);

        assertEquals(50, sampleItem.getStock()); // 100 - 50
        verify(itemRepository).save(sampleItem);
    }

    @Test
    void testAddInventory_Withdrawal_InsufficientStock() {
        Inventory inventory = new Inventory();
        inventory.setItemId(1L);
        inventory.setQty(150); // More than 100
        inventory.setType("W");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        assertThrows(InsufficientStockException.class, () -> stockService.addInventory(inventory));

        assertEquals(100, sampleItem.getStock()); // Should remain unchanged
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void testAddInventory_InvalidType() {
        Inventory inventory = new Inventory();
        inventory.setItemId(1L);
        inventory.setQty(10);
        inventory.setType("X"); // Invalid

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        assertThrows(IllegalArgumentException.class, () -> stockService.addInventory(inventory));
    }

    // --- ORDER TESTS ---

    @Test
    void testPlaceOrder_Success() {
        Order order = new Order();
        order.setOrderNo("ORD-001");
        order.setItemId(1L);
        order.setQty(10);
        order.setPrice(100.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = stockService.placeOrder(order);

        assertNotNull(result);
        assertEquals(90, sampleItem.getStock()); // 100 - 10
        verify(itemRepository).save(sampleItem);
        verify(orderRepository).save(order);
    }

    @Test
    void testPlaceOrder_InsufficientStock() {
        Order order = new Order();
        order.setOrderNo("ORD-002");
        order.setItemId(1L);
        order.setQty(200); // More than 100

        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        assertThrows(InsufficientStockException.class, () -> stockService.placeOrder(order));

        assertEquals(100, sampleItem.getStock());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
