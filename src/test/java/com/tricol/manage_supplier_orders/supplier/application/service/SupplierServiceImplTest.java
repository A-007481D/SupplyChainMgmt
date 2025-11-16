package com.tricol.manage_supplier_orders.supplier.application.service;

import com.tricol.manage_supplier_orders.supplier.application.ports.SupplierServicePort;
import com.tricol.manage_supplier_orders.supplier.domain.model.Supplier;
import com.tricol.manage_supplier_orders.supplier.domain.ports.SupplierRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService Unit Tests")
class SupplierServiceImplTest {

    @Mock
    private SupplierRepositoryPort supplierRepositoryPort;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private Supplier supplier;
    private SupplierServicePort.CreateSupplierCommand command;

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

        command = new SupplierServicePort.CreateSupplierCommand(
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
    @DisplayName("Create Supplier Tests")
    class CreateSupplierTests {

        @Test
        @DisplayName("Should create supplier successfully")
        void testCreateSupplier() {
            when(supplierRepositoryPort.createSupplier(any(Supplier.class)))
                    .thenReturn(supplier);

            Supplier result = supplierService.create(command);

            assertNotNull(result);
            assertEquals("TechSupply Inc", result.getCompany());
            assertEquals("john@techsupply.com", result.getEmail());
            verify(supplierRepositoryPort, times(1)).createSupplier(any(Supplier.class));
        }

        @Test
        @DisplayName("Should set all properties when creating supplier")
        void testCreateSupplierWithAllProperties() {
            when(supplierRepositoryPort.createSupplier(any(Supplier.class)))
                    .thenReturn(supplier);

            Supplier result = supplierService.create(command);

            assertEquals("John Doe", result.getContact());
            assertEquals("555-1234", result.getPhone());
            assertEquals("San Francisco", result.getCity());
            assertEquals("ICE123456", result.getIce());
        }
    }

    @Nested
    @DisplayName("Get Supplier Tests")
    class GetSupplierTests {

        @Test
        @DisplayName("Should get supplier by ID successfully")
        void testGetSupplierById() {
            when(supplierRepositoryPort.findSupplierById(1L))
                    .thenReturn(Optional.of(supplier));

            Supplier result = supplierService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("TechSupply Inc", result.getCompany());
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void testGetSupplierByIdNotFound() {
            when(supplierRepositoryPort.findSupplierById(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                supplierService.getById(999L);
            });
        }
    }

    @Nested
    @DisplayName("Update Supplier Tests")
    class UpdateSupplierTests {

        @Test
        @DisplayName("Should update supplier successfully")
        void testUpdateSupplier() {
            SupplierServicePort.CreateSupplierCommand updateCommand = new SupplierServicePort.CreateSupplierCommand(
                    "UpdatedTech Inc",
                    "456 New Street",
                    "Jane Smith",
                    "jane@updatedtech.com",
                    "555-9999",
                    "New York",
                    "ICE999999"
            );

            when(supplierRepositoryPort.findSupplierById(1L))
                    .thenReturn(Optional.of(supplier));
            when(supplierRepositoryPort.createSupplier(any(Supplier.class)))
                    .thenReturn(supplier);

            Supplier result = supplierService.update(1L, updateCommand);

            assertNotNull(result);
            verify(supplierRepositoryPort, times(1)).findSupplierById(1L);
            verify(supplierRepositoryPort, times(1)).createSupplier(any(Supplier.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent supplier")
        void testUpdateSupplierNotFound() {
            when(supplierRepositoryPort.findSupplierById(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                supplierService.update(999L, command);
            });
        }
    }

    @Nested
    @DisplayName("Delete Supplier Tests")
    class DeleteSupplierTests {

        @Test
        @DisplayName("Should delete supplier successfully")
        void testDeleteSupplier() {
            doNothing().when(supplierRepositoryPort).deleteSupplierById(1L);

            supplierService.delete(1L);

            verify(supplierRepositoryPort, times(1)).deleteSupplierById(1L);
        }
    }

    @Nested
    @DisplayName("List Suppliers Tests")
    class ListSuppliersTests {

        @Test
        @DisplayName("Should list suppliers with pagination")
        void testListSuppliers() {
            Page<Supplier> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(supplierRepositoryPort.findAll(pageable))
                    .thenReturn(page);

            Page<Supplier> result = supplierService.list(pageable);

            assertNotNull(result);
            verify(supplierRepositoryPort, times(1)).findAll(pageable);
        }
    }
}

