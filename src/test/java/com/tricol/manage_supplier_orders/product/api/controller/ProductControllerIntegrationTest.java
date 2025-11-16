package com.tricol.manage_supplier_orders.product.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.manage_supplier_orders.product.api.dto.ProductRequestDTO;
import com.tricol.manage_supplier_orders.product.application.ports.ProductServicePort;
import com.tricol.manage_supplier_orders.product.domain.enums.Category;
import com.tricol.manage_supplier_orders.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductServicePort productService;

    private Product product;
    private ProductRequestDTO requestDTO;

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

        requestDTO = new ProductRequestDTO(
                "Laptop",
                "High-performance laptop",
                1200.00,
                "EQUIPMENT",
                "piece",
                50,
                1L
        );
    }

    @Nested
    @DisplayName("Create Product API Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product with valid request")
        void testCreateProduct() throws Exception {
            when(productService.createProduct(any(Product.class)))
                    .thenReturn(product);

            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Laptop"));
        }

        @Test
        @DisplayName("Should return 400 for invalid request (negative price)")
        void testCreateProductInvalidPrice() throws Exception {
            ProductRequestDTO invalidDTO = new ProductRequestDTO(
                    "Product",
                    "Description",
                    -100.0, // Invalid negative price
                    "EQUIPMENT",
                    "piece",
                    10,
                    1L
            );

            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Get Product API Tests")
    class GetProductTests {

        @Test
        @DisplayName("Should get product by ID")
        void testGetProduct() throws Exception {
            when(productService.getProductById(1L))
                    .thenReturn(product);

            mockMvc.perform(get("/api/v1/products/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Laptop"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent product")
        void testGetProductNotFound() throws Exception {
            when(productService.getProductById(999L))
                    .thenThrow(new IllegalArgumentException("Product not found"));

            mockMvc.perform(get("/api/v1/products/999"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Update Product API Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product")
        void testUpdateProduct() throws Exception {
            when(productService.updateProduct(eq(1L), any(Product.class)))
                    .thenReturn(product);

            mockMvc.perform(put("/api/v1/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Get All Products API Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Should get all products with pagination")
        void testGetAllProducts() throws Exception {
            Page<Product> page = new PageImpl<>(new ArrayList<>(List.of(product)));

            when(productService.getAllProducts(any()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("Search Products API Tests")
    class SearchProductsTests {

        @Test
        @DisplayName("Should search products by name")
        void testSearchProducts() throws Exception {
            Page<Product> page = new PageImpl<>(new ArrayList<>(List.of(product)));

            when(productService.searchProductsByName(eq("Laptop"), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/products/search?name=Laptop")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("Get Products by Supplier API Tests")
    class GetProductsBySupplierTests {

        @Test
        @DisplayName("Should get products by supplier")
        void testGetProductsBySupplier() throws Exception {
            List<Product> products = new ArrayList<>(List.of(product));

            when(productService.getProductsBySupplier(1L))
                    .thenReturn(products);

            mockMvc.perform(get("/api/v1/products/supplier/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Get Low Stock Products API Tests")
    class GetLowStockProductsTests {

        @Test
        @DisplayName("Should get low stock products")
        void testGetLowStockProducts() throws Exception {
            List<Product> products = new ArrayList<>(List.of(product));

            when(productService.getLowStockProducts(10))
                    .thenReturn(products);

            mockMvc.perform(get("/api/v1/products/low-stock?threshold=10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Get Products by Category API Tests")
    class GetProductsByCategoryTests {

        @Test
        @DisplayName("Should get products by category")
        void testGetProductsByCategory() throws Exception {
            List<Product> products = new ArrayList<>(List.of(product));

            when(productService.getProductsByCategory(Category.EQUIPMENT))
                    .thenReturn(products);

            mockMvc.perform(get("/api/v1/products/category/EQUIPMENT")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Delete Product API Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product")
        void testDeleteProduct() throws Exception {
            mockMvc.perform(delete("/api/v1/products/1"))
                    .andExpect(status().isNoContent());
        }
    }
}

