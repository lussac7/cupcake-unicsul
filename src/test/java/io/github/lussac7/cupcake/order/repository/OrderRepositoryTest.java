package io.github.lussac7.cupcake.order.repository;

import io.github.lussac7.cupcake.order.entity.Order;
import io.github.lussac7.cupcake.order.entity.OrderItem;
import io.github.lussac7.cupcake.product.entity.Product;
import io.github.lussac7.cupcake.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldPersistOrderWithItems() {
        Product product = new Product(
                "Cupcake de Chocolate",
                "Cupcake com cobertura de chocolate",
                new BigDecimal("10.00"),
                null
        );

        productRepository.saveAndFlush(product);

        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        order.addItem(product, 2);

        Order savedOrder = orderRepository.saveAndFlush(order);

        assertNotNull(savedOrder.getId());
        assertNotNull(savedOrder.getCreatedAt());

        assertEquals(1, savedOrder.getItems().size());

        OrderItem savedItem = savedOrder.getItems().getFirst();

        assertNotNull(savedItem.getId());
        assertNotNull(savedItem.getOrder());
        assertNotNull(savedItem.getProduct());

        assertEquals(savedOrder.getId(), savedItem.getOrder().getId());
        assertEquals(product.getId(), savedItem.getProduct().getId());

        assertEquals("Cupcake de Chocolate", savedItem.getProductName());

        assertEquals(0, savedItem.getSubtotal().compareTo(
                new BigDecimal("20.00")
        ));

        assertEquals(0, savedOrder.getTotal().compareTo(
                new BigDecimal("20.00")
        ));
    }

    @Test
    void shouldRejectPersistingOrderWithoutItems() {
        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        assertThrows(
                RuntimeException.class,
                () -> orderRepository.saveAndFlush(order)
        );
    }
}