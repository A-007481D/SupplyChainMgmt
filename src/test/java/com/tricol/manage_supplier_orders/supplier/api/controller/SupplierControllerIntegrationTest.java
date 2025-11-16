package com.tricol.manage_supplier_orders.supplier.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.manage_supplier_orders.supplier.api.dto.SupplierRequestDTO;
import com.tricol.manage_supplier_orders.supplier.application.ports.SupplierServicePort;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SupplierController Integration Tests")
class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierServicePort supplierServicePort;

    private Supplier supplier;
    private SupplierRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        supplier = Supplier.builder()
                .id(1L)
                .company("TechSupply Inc")
                .address("123 Tech Street")
                .contact("John Doe")
                .email("john@techsupply.com")
                .phone("555-1234")
                .city("San Francisco")
                .ice("ICE123456")
                .build();

        requestDTO = new SupplierRequestDTO(
                "TechSupply Inc",
                "123 Tech Street",
                "John Doe",
                "john@techsupply.com",
                "555-1234",
                "San Francisco",
                "ICE123456"
        );
    }

    @Nested
    @DisplayName("Create Supplier API Tests")
    class CreateSupplierTests {

        @Test
        @DisplayName("Should create supplier with valid request")
        void testCreateSupplier() throws Exception {
            when(supplierServicePort.create(any()))
                    .thenReturn(supplier);

            mockMvc.perform(post("/api/v1/suppliers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.company").value("TechSupply Inc"));
        }

        @Test
        @DisplayName("Should return 400 for invalid request")
        void testCreateSupplierInvalidRequest() throws Exception {
            SupplierRequestDTO invalidDTO = new SupplierRequestDTO(
                    "", // Empty company
                    "Address",
                    "Contact",
                    "email@test.com",
                    "555-1234",
                    "City",
                    "ICE123"
            );

            mockMvc.perform(post("/api/v1/suppliers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Get Supplier API Tests")
    class GetSupplierTests {

        @Test
        @DisplayName("Should get supplier by ID")
        void testGetSupplierById() throws Exception {
            when(supplierServicePort.getById(1L))
                    .thenReturn(supplier);

            mockMvc.perform(get("/api/v1/suppliers/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.company").value("TechSupply Inc"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent supplier")
        void testGetSupplierNotFound() throws Exception {
            when(supplierServicePort.getById(999L))
                    .thenThrow(new IllegalArgumentException("Supplier not found"));

            mockMvc.perform(get("/api/v1/suppliers/999")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Update Supplier API Tests")
    class UpdateSupplierTests {

        @Test
        @DisplayName("Should update supplier")
        void testUpdateSupplier() throws Exception {
            Supplier updated = supplier;
            updated.setCompany("UpdatedTech Inc");

            when(supplierServicePort.update(any(), any()))
                    .thenReturn(updated);

            mockMvc.perform(put("/api/v1/suppliers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete Supplier API Tests")
    class DeleteSupplierTests {

        @Test
        @DisplayName("Should delete supplier")
        void testDeleteSupplier() throws Exception {
            mockMvc.perform(delete("/api/v1/suppliers/1"))
                    .andExpect(status().isNoContent());
        }
    }
}

