package com.example.stock.controller;

import com.example.stock.model.Order;
import com.example.stock.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/orders - Getting all orders (page: {}, size: {})", page, size);
        Page<Order> orders = stockService.getAllOrders(PageRequest.of(page, size));
        log.info("Successfully retrieved {} orders", orders.getTotalElements());
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        log.info("POST /api/orders - Placing order: {} for {} units of item ID {}",
                order.getOrderNo(), order.getQty(), order.getItemId());
        Order savedOrder = stockService.placeOrder(order);
        log.info("Successfully placed order {} with ID: {}", savedOrder.getOrderNo(), savedOrder.getId());
        return ResponseEntity.ok(savedOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        log.info("PUT /api/orders/{} - Updating order", id);
        Order updatedOrder = stockService.updateOrder(id, order);
        log.info("Successfully updated order: {}", updatedOrder.getOrderNo());
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /api/orders/{} - Deleting order", id);
        stockService.deleteOrder(id);
        log.info("Successfully deleted order with ID: {}", id);
        return ResponseEntity.ok().build();
    }
}
