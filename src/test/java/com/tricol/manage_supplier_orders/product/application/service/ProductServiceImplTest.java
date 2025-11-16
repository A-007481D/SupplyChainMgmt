package com.tricol.manage_supplier_orders.product.application.service;

import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import com.tricol.manage_supplier_orders.product.domain.ports.ProductRepositoryPort;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setPrice(1200.00);
        product.setCategory(Category.EQUIPMENT);
        product.setUnit("piece");
        product.setStockQuantity(50);
        product.setSupplierId(1L);
        product.setAverageCost(1000.00);
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void testCreateProduct() {
            when(productRepositoryPort.save(any(Product.class)))
                    .thenReturn(product);

            Product result = productService.createProduct(product);

            assertNotNull(result);
            assertEquals("Laptop", result.getName());
            assertEquals(1200.00, result.getPrice());
            verify(productRepositoryPort, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should save all product properties")
        void testCreateProductWithAllProperties() {
            when(productRepositoryPort.save(any(Product.class)))
                    .thenReturn(product);

            Product result = productService.createProduct(product);

            assertEquals(Category.EQUIPMENT, result.getCategory());
            assertEquals(50, result.getStockQuantity());
            assertEquals(1L, result.getSupplierId());
        }
    }

    @Nested
    @DisplayName("Get Product Tests")
    class GetProductTests {

        @Test
        @DisplayName("Should get product by ID successfully")
        void testGetProductById() {
            when(productRepositoryPort.findById(1L))
                    .thenReturn(Optional.of(product));

            Product result = productService.getProductById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Laptop", result.getName());
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void testGetProductByIdNotFound() {
            when(productRepositoryPort.findById(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                productService.getProductById(999L);
            });
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully")
        void testUpdateProduct() {
            Product updatedProduct = new Product();
            updatedProduct.setName("Updated Laptop");
            updatedProduct.setPrice(1500.00);
            updatedProduct.setCategory(Category.EQUIPMENT);

            when(productRepositoryPort.findById(1L))
                    .thenReturn(Optional.of(product));
            when(productRepositoryPort.save(any(Product.class)))
                    .thenReturn(product);

            Product result = productService.updateProduct(1L, updatedProduct);

            assertNotNull(result);
            verify(productRepositoryPort, times(1)).findById(1L);
            verify(productRepositoryPort, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent product")
        void testUpdateProductNotFound() {
            when(productRepositoryPort.findById(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                productService.updateProduct(999L, product);
            });
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully")
        void testDeleteProduct() {
            doNothing().when(productRepositoryPort).deleteById(1L);

            productService.deleteProduct(1L);

            verify(productRepositoryPort, times(1)).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Search Product Tests")
    class SearchProductTests {

        @Test
        @DisplayName("Should search products by name")
        void testSearchProductsByName() {
            Page<Product> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(productRepositoryPort.findByNameContainingIgnoreCase("Laptop", pageable))
                    .thenReturn(page);

            Page<Product> result = productService.searchProductsByName("Laptop", pageable);

            assertNotNull(result);
            verify(productRepositoryPort, times(1)).findByNameContainingIgnoreCase("Laptop", pageable);
        }

        @Test
        @DisplayName("Should get products by supplier")
        void testGetProductsBySupplier() {
            List<Product> products = new ArrayList<>(List.of(product));

            when(productRepositoryPort.findBySupplierId(1L))
                    .thenReturn(products);

            List<Product> result = productService.getProductsBySupplier(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(productRepositoryPort, times(1)).findBySupplierId(1L);
        }

        @Test
        @DisplayName("Should get products by category")
        void testGetProductsByCategory() {
            List<Product> products = new ArrayList<>(List.of(product));

            when(productRepositoryPort.findProductsByCategory(Category.EQUIPMENT))
                    .thenReturn(products);

            List<Product> result = productService.getProductsByCategory(Category.EQUIPMENT);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(productRepositoryPort, times(1)).findProductsByCategory(Category.EQUIPMENT);
        }

        @Test
        @DisplayName("Should get low stock products")
        void testGetLowStockProducts() {
            List<Product> products = new ArrayList<>(List.of(product));

            when(productRepositoryPort.findLowStockProducts(10))
                    .thenReturn(products);

            List<Product> result = productService.getLowStockProducts(10);

            assertNotNull(result);
            verify(productRepositoryPort, times(1)).findLowStockProducts(10);
        }
    }

    @Nested
    @DisplayName("List Products Tests")
    class ListProductsTests {

        @Test
        @DisplayName("Should list all products with pagination")
        void testGetAllProducts() {
            Page<Product> page = new PageImpl<>(new ArrayList<>());
            PageRequest pageable = PageRequest.of(0, 10);

            when(productRepositoryPort.findAll(pageable))
                    .thenReturn(page);

            Page<Product> result = productService.getAllProducts(pageable);

            assertNotNull(result);
            verify(productRepositoryPort, times(1)).findAll(pageable);
        }
    }
}

