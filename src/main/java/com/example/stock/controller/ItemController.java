package com.example.stock.controller;

import com.example.stock.model.Item;
import com.example.stock.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<Page<Item>> getAllItems(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/items - Getting all items (page: {}, size: {})", page, size);
        Page<Item> items = stockService.getAllItems(PageRequest.of(page, size));
        log.info("Successfully retrieved {} items", items.getTotalElements());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        log.info("GET /api/items/{} - Getting item by ID", id);
        return stockService.getItem(id)
                .map(item -> {
                    log.info("Successfully found item: {}", item.getName());
                    return ResponseEntity.ok(item);
                })
                .orElseGet(() -> {
                    log.warn("Item with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        log.info("POST /api/items - Creating new item: {}", item.getName());
        Item savedItem = stockService.saveItem(item);
        log.info("Successfully created item with ID: {}", savedItem.getId());
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        log.info("PUT /api/items/{} - Updating item", id);
        Item updatedItem = stockService.updateItem(id, item);
        log.info("Successfully updated item: {}", updatedItem.getName());
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        log.info("DELETE /api/items/{} - Deleting item", id);
        stockService.deleteItem(id);
        log.info("Successfully deleted item with ID: {}", id);
        return ResponseEntity.ok().build();
    }
}
