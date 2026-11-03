package io.github.lussac7.cupcake.product.repository;

import io.github.lussac7.cupcake.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldFindOnlyActiveProducts() {
        Product activeProduct = new Product(
                "Chocolate Cupcake",
                "Chocolate cupcake",
                new BigDecimal("12.00"),
                null
        );

        Product inactiveProduct = new Product(
                "Vanilla Cupcake",
                "Vanilla cupcake",
                new BigDecimal("10.00"),
                null
        );

        inactiveProduct.deactivate();

        productRepository.saveAndFlush(activeProduct);
        productRepository.saveAndFlush(inactiveProduct);

        List<Product> activeProducts =
                productRepository.findAllByActiveTrue();

        assertEquals(1, activeProducts.size());
        assertEquals(
                "Chocolate Cupcake",
                activeProducts.getFirst().getName()
        );
        assertTrue(activeProducts.getFirst().isActive());
    }

    @Test
    void shouldFindOnlyActiveProductsWithPagination() {
        Product product1 = new Product(
                "Chocolate Cupcake",
                "Chocolate cupcake",
                new BigDecimal("12.00"),
                null
        );

        Product product2 = new Product(
                "Vanilla Cupcake",
                "Vanilla cupcake",
                new BigDecimal("10.00"),
                null
        );

        Product product3 = new Product(
                "Strawberry Cupcake",
                "Strawberry cupcake",
                new BigDecimal("11.00"),
                null
        );

        Product inactiveProduct = new Product(
                "Lemon Cupcake",
                "Lemon cupcake",
                new BigDecimal("9.00"),
                null
        );

        inactiveProduct.deactivate();

        productRepository.saveAllAndFlush(
                List.of(product1, product2, product3, inactiveProduct)
        );

        Pageable pageable = PageRequest.of(0, 2);

        Page<Product> page =
                productRepository.findAllByActiveTrue(pageable);

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertTrue(page.getContent().stream()
                .allMatch(Product::isActive));
    }

    @Test
    void shouldReturnCorrectPageWhenNavigatingActiveProducts() {
        Product product1 = new Product(
                "Chocolate Cupcake",
                "Chocolate cupcake",
                new BigDecimal("12.00"),
                null
        );

        Product product2 = new Product(
                "Vanilla Cupcake",
                "Vanilla cupcake",
                new BigDecimal("10.00"),
                null
        );

        Product product3 = new Product(
                "Strawberry Cupcake",
                "Strawberry cupcake",
                new BigDecimal("11.00"),
                null
        );

        productRepository.saveAllAndFlush(
                List.of(product1, product2, product3)
        );

        Pageable pageable = PageRequest.of(
                1,
                2,
                Sort.by("id").ascending()
        );

        Page<Product> page =
                productRepository.findAllByActiveTrue(pageable);

        assertEquals(1, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(1, page.getNumber());
        assertEquals("Strawberry Cupcake",
                page.getContent().getFirst().getName());
    }
}