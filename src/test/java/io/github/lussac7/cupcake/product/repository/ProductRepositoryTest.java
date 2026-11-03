package io.github.lussac7.cupcake.product.repository;

import io.github.lussac7.cupcake.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

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
}