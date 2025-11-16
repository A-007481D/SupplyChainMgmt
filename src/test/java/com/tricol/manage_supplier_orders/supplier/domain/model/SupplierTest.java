package com.tricol.manage_supplier_orders.supplier.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Supplier Domain Tests")
class SupplierTest {

    private Supplier supplier;

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
    }

    @Nested
    @DisplayName("Supplier Creation Tests")
    class SupplierCreationTests {

        @Test
        @DisplayName("Should create supplier with builder")
        void testCreateSupplierWithBuilder() {
            assertNotNull(supplier);
            assertEquals(1L, supplier.getId());
            assertEquals("TechSupply Inc", supplier.getCompany());
            assertEquals("123 Tech Street", supplier.getAddress());
        }

        @Test
        @DisplayName("Should create supplier with all properties")
        void testSupplierProperties() {
            assertEquals("John Doe", supplier.getContact());
            assertEquals("john@techsupply.com", supplier.getEmail());
            assertEquals("555-1234", supplier.getPhone());
            assertEquals("San Francisco", supplier.getCity());
            assertEquals("ICE123456", supplier.getIce());
        }
    }

    @Nested
    @DisplayName("Supplier Property Tests")
    class SupplierPropertyTests {

        @Test
        @DisplayName("Should set and get company name")
        void testCompanyName() {
            supplier.setCompany("NewTech Ltd");
            assertEquals("NewTech Ltd", supplier.getCompany());
        }

        @Test
        @DisplayName("Should set and get address")
        void testAddress() {
            supplier.setAddress("456 Supply Ave");
            assertEquals("456 Supply Ave", supplier.getAddress());
        }

        @Test
        @DisplayName("Should set and get contact person")
        void testContact() {
            supplier.setContact("Jane Smith");
            assertEquals("Jane Smith", supplier.getContact());
        }

        @Test
        @DisplayName("Should set and get email")
        void testEmail() {
            supplier.setEmail("jane@newtech.com");
            assertEquals("jane@newtech.com", supplier.getEmail());
        }

        @Test
        @DisplayName("Should set and get phone")
        void testPhone() {
            supplier.setPhone("555-9999");
            assertEquals("555-9999", supplier.getPhone());
        }

        @Test
        @DisplayName("Should set and get city")
        void testCity() {
            supplier.setCity("New York");
            assertEquals("New York", supplier.getCity());
        }

        @Test
        @DisplayName("Should set and get ICE")
        void testIce() {
            supplier.setIce("ICE999999");
            assertEquals("ICE999999", supplier.getIce());
        }
    }

    @Nested
    @DisplayName("Supplier Validation Tests")
    class SupplierValidationTests {

        @Test
        @DisplayName("Should allow supplier with null ID (new supplier)")
        void testSupplierWithNullId() {
            Supplier newSupplier = Supplier.builder()
                    .company("New Company")
                    .build();
            assertNull(newSupplier.getId());
            assertNotNull(newSupplier.getCompany());
        }

        @Test
        @DisplayName("Should handle minimal supplier information")
        void testMinimalSupplierInformation() {
            Supplier minimalSupplier = Supplier.builder()
                    .company("Company Name")
                    .build();

            assertEquals("Company Name", minimalSupplier.getCompany());
            assertNull(minimalSupplier.getAddress());
            assertNull(minimalSupplier.getContact());
            assertNull(minimalSupplier.getEmail());
        }
    }
}

