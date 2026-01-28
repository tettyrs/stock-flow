package com.example.stock.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "item", indexes = {
        @Index(name = "idx_item_name", columnList = "name"),
        @Index(name = "idx_item_stock", columnList = "stock")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    // Eagerly updated stock count for listing performance
    @Column(columnDefinition = "integer default 0")
    private Integer stock = 0;
}
