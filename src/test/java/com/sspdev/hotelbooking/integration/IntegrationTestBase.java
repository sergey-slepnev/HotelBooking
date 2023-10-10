package com.sspdev.hotelbooking.integration;

import com.sspdev.hotelbooking.integration.annotation.IT;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

@IT
@Sql("/sql/data.sql")
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
public abstract class IntegrationTestBase {

    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15.4");

    @BeforeAll
    static void startContainer() {
        container.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }
}