-- ============================================
-- Database Initialization Script
-- Executed ONLY on first application startup
-- ============================================

-- Create ITEM table
CREATE TABLE IF NOT EXISTS item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    stock INT NOT NULL DEFAULT 0 COMMENT 'Real-time stock count'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product/Item master data';

-- Create indexes for ITEM based on controller queries
-- ItemController.getAllItems() - pagination and searching by name
CREATE INDEX idx_item_name ON item(name);
-- ItemController queries may filter/sort by price
CREATE INDEX idx_item_price ON item(price);
-- ItemController queries may filter/sort by stock
CREATE INDEX idx_item_stock ON item(stock);

-- Create ORDER table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    item_id BIGINT NOT NULL,
    qty INT NOT NULL,
    price DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer orders';

-- Create indexes for ORDER based on controller queries
-- OrderController.getAllOrders() - searches by order_no
CREATE INDEX idx_order_no ON orders(order_no);
-- OrderController queries by item_id (FK lookup)
CREATE INDEX idx_order_item_id ON orders(item_id);
-- OrderController may filter/sort by created_at for reporting
CREATE INDEX idx_order_created_at ON orders(created_at);
-- OrderController may filter/sort by price
CREATE INDEX idx_order_price ON orders(price);

-- Create INVENTORY table
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    qty INT NOT NULL,
    type VARCHAR(10) NOT NULL COMMENT 'T=Top-Up, W=Withdrawal',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Inventory transactions (Top-Up/Withdrawal)';

-- Create indexes for INVENTORY based on controller queries
-- InventoryController.getAllInventory() - filter by item_id
CREATE INDEX idx_inventory_item_id ON inventory(item_id);
-- InventoryController may filter by type (T/W)
CREATE INDEX idx_inventory_type ON inventory(type);
-- InventoryController may filter/sort by created_at for reporting
CREATE INDEX idx_inventory_created_at ON inventory(created_at);

-- Insert sample data (based on the uploaded image)
-- ITEM data
INSERT IGNORE INTO item (id, name, price, stock) VALUES
(1, 'Pen', 5, 0),
(2, 'Book', 10, 0),
(3, 'Bag', 30, 0),
(4, 'Pencil', 3, 0),
(5, 'Shoe', 45, 0),
(6, 'Box', 5, 0),
(7, 'Cap', 25, 0);

-- INVENTORY data (Top-Up transactions)
INSERT IGNORE INTO inventory (id, item_id, qty, type) VALUES
(1, 1, 5, 'T'),
(2, 2, 10, 'T'),
(3, 3, 30, 'T'),
(4, 4, 3, 'T'),
(5, 5, 45, 'T'),
(6, 6, 5, 'T'),
(7, 7, 25, 'T'),
(8, 4, 7, 'T'),
(9, 5, 10, 'W');

-- ORDER data
INSERT IGNORE INTO orders (order_no, item_id, qty, price) VALUES
('O1', 1, 2, 5),
('O2', 2, 3, 10),
('O3', 5, 4, 45),
('O4', 4, 1, 2),
('O5', 5, 2, 45),
('O6', 6, 3, 5),
('O7', 1, 5, 5),
('O8', 2, 4, 10),
('O9', 3, 2, 30),
('O10', 4, 3, 3);
