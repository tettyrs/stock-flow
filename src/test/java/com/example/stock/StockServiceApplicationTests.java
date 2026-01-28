package com.example.stock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StockServiceApplicationTests {

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.example.stock.config.DatabaseInitializer databaseInitializer;

    @Test
    void contextLoads() {
    }

}
