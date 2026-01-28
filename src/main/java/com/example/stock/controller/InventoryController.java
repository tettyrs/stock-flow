package com.example.stock.controller;

import com.example.stock.model.Inventory;
import com.example.stock.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<Page<Inventory>> getAllInventory(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/inventory - Getting all inventory transactions (page: {}, size: {})", page, size);
        Page<Inventory> inventory = stockService.getAllInventory(PageRequest.of(page, size));
        log.info("Successfully retrieved {} inventory transactions", inventory.getTotalElements());
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    public ResponseEntity<Inventory> addInventory(@RequestBody Inventory inventory) {
        String transactionType = "T".equals(inventory.getType()) ? "Top-Up" : "Withdrawal";
        log.info("POST /api/inventory - Adding inventory transaction: {} {} units for item ID {}",
                transactionType, inventory.getQty(), inventory.getItemId());
        Inventory savedInventory = stockService.addInventory(inventory);
        log.info("Successfully added inventory transaction with ID: {}", savedInventory.getId());
        return ResponseEntity.ok(savedInventory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        log.info("PUT /api/inventory/{} - Updating inventory transaction", id);
        Inventory updatedInventory = stockService.updateInventory(id, inventory);
        log.info("Successfully updated inventory transaction ID: {}", id);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        log.info("DELETE /api/inventory/{} - Deleting inventory transaction", id);
        stockService.deleteInventory(id);
        log.info("Successfully deleted inventory transaction with ID: {}", id);
        return ResponseEntity.ok().build();
    }
}
