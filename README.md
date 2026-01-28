# StockFlow

A Spring Boot application ("StockFlow") for managing stock items, inventory transactions, and orders.

## Prerequisites
- **Java 17** (JDK 17.0.10 or higher)
- MySQL 8.0+
- Maven (or use included Maven wrapper)

## Configuration

### Environment Variables (.env)
The application uses a `.env` file in the root directory for database configuration:

```properties
DB_HOST=[hostname]
DB_PORT=[port]
DB_NAME=[db-name]
DB_USERNAME=[username]
DB_PASSWORD=[password]
```

### Database Setup
1. Create the MySQL database:
   *(The name `project_db` is an example. Ensure this matches the `DB_NAME` in your `.env` file)*
   ```sql
   CREATE DATABASE project_db;
   ```

2. Tables will be **automatically created on first run**:
   - The `DatabaseInitializer` component executes `db-init.sql` on first startup
   - Creates tables: `item`, `orders`, `inventory`
   - Creates optimized indexes based on controller queries
   - Inserts sample data
   - **Only executes once** - skips if tables already exist

### Database Schema
Three tables are automatically created with optimized indexes:

#### Item Table
- Stores product/item information with real-time stock count
- **Indexes**: `idx_item_name`, `idx_item_price`, `idx_item_stock`

#### Orders Table
- Stores customer orders
- **Indexes**: `idx_order_no`, `idx_order_item_id`, `idx_order_created_at`, `idx_order_price`

#### Inventory Table
- Tracks inventory transactions (Top-Up and Withdrawal)
- Type: `T` = Top-Up, `W` = Withdrawal
- **Indexes**: `idx_inventory_item_id`, `idx_inventory_type`, `idx_inventory_created_at`

## Running the Application

### Using Maven Wrapper

The Maven wrapper is configured to automatically use Java 17:

```bash
# Clean and compile
.\mvnw.cmd clean compile

# Run tests
.\mvnw.cmd test

# Run the application
.\mvnw.cmd spring-boot:run
```

### First Run - Database Initialization

On the first run, you'll see:
```
[DATABASE INITIALIZER] Checking if database initialization is needed...
[DATABASE INITIALIZER] Tables not found. Executing db-init.sql...
[DATABASE INITIALIZER] ✓ Database initialization completed successfully!
[DATABASE INITIALIZER] ✓ Created tables: item, orders, inventory
[DATABASE INITIALIZER] ✓ Created indexes for optimal query performance
[DATABASE INITIALIZER] ✓ Inserted sample data
```

On subsequent runs:
```
[DATABASE INITIALIZER] Tables already exist. Skipping initialization.
```

## API Endpoints

The application will be available at: `http://localhost:8080`

- **Items**: `/api/items`
- **Inventory**: `/api/inventory`
- **Orders**: `/api/orders`

## Testing
Run tests with H2 in-memory database:
```bash
.\mvnw.cmd test
```

## API Testing
Import `postman_collection.json` into Postman or Insomnia to test all endpoints.

## Logging
   
The application includes comprehensive logging:
- Controller operations (requests, parameters, responses)
- Service business logic (stock updates, transactions, orders)
- Database operations (SQL queries)
- Application initialization and startup
