# Developer Guide & Troubleshooting

This document contains technical details, troubleshooting tips, and reference links for developers working on StockFlow.

## Technology Stack
- **Framework**: Spring Boot 3.3.7
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, H2 Database (Test scope)

## Project Structure
```text
src/main/java
└── com.example.stock
    ├── config       # App configurations (DB Init, CORS, etc.)
    ├── controller   # REST API Controllers
    ├── model        # JPA Entities (Item, Inventory, Order)
    ├── repository   # Data Access Layer (Spring Data JPA)
    └── service      # Business Logic
```

## Advanced Testing

### Running Specific Tests
To run only the Service layer tests:
```bash
./mvnw -Dtest=StockServiceTest test
```

To run a specific Controller test:
```bash
./mvnw -Dtest=ItemControllerTest test
```

### debugging Tests
Tests use an in-memory H2 database to isolate them from your running MySQL instance. Configuration is located in `src/test/resources/application.properties`.

## Troubleshooting

### 1. Database Connection Failed
**Error**: `Communications link failure`
- **Solution**: 
  - Ensure MySQL is running.
  - Check `DB_HOST` and `DB_PORT` in `.env`.
  - Verify credentials (`DB_USERNAME`, `DB_PASSWORD`).

### 2. Port Already in Use
**Error**: `Web server failed to start. Port 8080 was already in use.`
- **Solution**:
  - Stop the other process using port 8080.
  - OR change the port in `src/main/resources/application.properties`:
    ```properties
    server.port=8081
    ```

### 3. "Release version 17 not supported"
- **Solution**: Ensure your `JAVA_HOME` points to JDK 17 or higher. Check with:
  ```bash
  ./mvnw -v
  ```

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.0/maven-plugin/reference/html/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/#data.sql.jpa-and-spring-data-jpa)
