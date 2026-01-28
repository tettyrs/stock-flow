package com.example.stock.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", indexes = {
        @Index(name = "idx_inventory_item_id", columnList = "item_id"),
        @Index(name = "idx_inventory_type", columnList = "type"),
        @Index(name = "idx_inventory_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false, length = 10)
    private String type; // "T" or "W"

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
