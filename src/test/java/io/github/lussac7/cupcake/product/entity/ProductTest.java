package io.github.lussac7.cupcake.product.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    @Test
    void shouldCreateProductWithValidData() {
        Product product = new Product(
                " Chocolate Cupcake ",
                " Delicious chocolate cupcake ",
                new BigDecimal("12.50"),
                " https://example.com/cupcake.jpg "
        );

        assertEquals("Chocolate Cupcake", product.getName());
        assertEquals("Delicious chocolate cupcake", product.getDescription());
        assertEquals(new BigDecimal("12.50"), product.getPrice());
        assertEquals("https://example.com/cupcake.jpg", product.getImageUrl());
        assertTrue(product.isActive());
    }

    @Test
    void shouldAllowProductWithoutImageUrl() {
        Product product = new Product(
                "Vanilla Cupcake",
                "Vanilla cupcake",
                new BigDecimal("10.00"),
                null
        );

        assertNull(product.getImageUrl());
    }

    @Test
    void shouldRejectBlankProductName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(
                        "   ",
                        "Valid description",
                        new BigDecimal("10.00"),
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankProductDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(
                        "Valid name",
                        "   ",
                        new BigDecimal("10.00"),
                        null
                )
        );
    }

    @Test
    void shouldRejectNonPositivePrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(
                        "Cupcake",
                        "Description",
                        BigDecimal.ZERO,
                        null
                )
        );

        assertThrows(IllegalArgumentException.class, () ->
                new Product(
                        "Cupcake",
                        "Description",
                        new BigDecimal("-1.00"),
                        null
                )
        );
    }

    @Test
    void shouldUpdateProductDetails() {
        Product product = new Product(
                "Vanilla Cupcake",
                "Original description",
                new BigDecimal("10.00"),
                null
        );

        product.updateDetails(
                "Chocolate Cupcake",
                "Updated description",
                new BigDecimal("15.00"),
                "https://example.com/chocolate.jpg"
        );

        assertEquals("Chocolate Cupcake", product.getName());
        assertEquals("Updated description", product.getDescription());
        assertEquals(new BigDecimal("15.00"), product.getPrice());
        assertEquals(
                "https://example.com/chocolate.jpg",
                product.getImageUrl()
        );
    }

    @Test
    void shouldDeactivateProduct() {
        Product product = new Product(
                "Cupcake",
                "Description",
                new BigDecimal("10.00"),
                null
        );

        product.deactivate();

        assertFalse(product.isActive());
    }

    @Test
    void shouldReactivateProduct() {
        Product product = new Product(
                "Cupcake",
                "Description",
                new BigDecimal("10.00"),
                null
        );

        product.deactivate();
        product.activate();

        assertTrue(product.isActive());
    }

    @Test
    void shouldRejectNullPrice() {
        assertThrows(NullPointerException.class, () ->
                new Product(
                        "Cupcake",
                        "Description",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldPreserveExistingDataWhenUpdateIsInvalid() {
        Product product = new Product(
                "Cupcake",
                "Original description",
                new BigDecimal("10.00"),
                null
        );

        assertThrows(IllegalArgumentException.class, () ->
                product.updateDetails(
                        "Updated cupcake",
                        "Updated description",
                        BigDecimal.ZERO,
                        null
                )
        );

        assertEquals("Cupcake", product.getName());
        assertEquals("Original description", product.getDescription());
        assertEquals(new BigDecimal("10.00"), product.getPrice());
    }
}