package com.tricol.manage_supplier_orders.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;


@TestConfiguration
public class TestcontainersConfiguration {

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("test_db")
                .withUsername("test_user")
                .withPassword("test_password")
                .withInitScript("db/init.sql");

        container.start();
        return container;
    }
}

