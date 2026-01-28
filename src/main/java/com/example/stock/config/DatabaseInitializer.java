package com.example.stock.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Database Initializer - Executes db-init.sql ONLY on first application startup
 * Checks if tables exist before executing initialization
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("[DATABASE INITIALIZER] Checking if database initialization is needed...");

        boolean tablesExist = checkIfTablesExist();

        if (tablesExist) {
            log.info("[DATABASE INITIALIZER] Tables already exist. Skipping initialization.");
            return;
        }

        log.info("[DATABASE INITIALIZER] Tables not found. Executing db-init.sql...");

        try (Connection connection = dataSource.getConnection()) {
            // Execute the SQL script
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db-init.sql"));
            log.info("[DATABASE INITIALIZER] ✓ Database initialization completed successfully!");
            log.info("[DATABASE INITIALIZER] ✓ Created tables: item, orders, inventory");
            log.info("[DATABASE INITIALIZER] ✓ Created indexes for optimal query performance");
            log.info("[DATABASE INITIALIZER] ✓ Inserted sample data");
        } catch (Exception e) {
            log.error("[DATABASE INITIALIZER] ✗ Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }


    private boolean checkIfTablesExist() {
        try {

            Integer itemCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item", Integer.class);
            if (itemCount == null || itemCount == 0) {
                log.info("[DATABASE INITIALIZER] Item table empty or missing data. Will run initialization.");
                return false;
            }

            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inventory", Integer.class);

            return true;
        } catch (Exception e) {
            log.info("[DATABASE INITIALIZER] Requesting initialization (Tables missing or error: {})", e.getMessage());
            return false;
        }
    }
}
