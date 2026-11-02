package io.github.lussac7.cupcake.order.entity;

import io.github.lussac7.cupcake.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidCustomerDetails() {
        Order order = new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );

        assertEquals("Maria Silva", order.getCustomerName());
        assertEquals("maria@example.com", order.getCustomerEmail());
        assertEquals("Rua das Flores, 100", order.getDeliveryAddress());
        assertEquals(BigDecimal.ZERO, order.getTotal());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void shouldRejectBlankCustomerName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order("", "maria@example.com", "Rua das Flores, 100")
        );
    }

    @Test
    void shouldRejectBlankCustomerEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order("Maria Silva", " ", "Rua das Flores, 100")
        );
    }

    @Test
    void shouldRejectBlankDeliveryAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order("Maria Silva", "maria@example.com", " ")
        );
    }

    @Test
    void shouldRejectNullProduct() {
        Order order = createOrder();

        assertThrows(
                NullPointerException.class,
                () -> order.addItem(null, 1)
        );
    }

    @Test
    void shouldRejectInactiveProduct() {
        Order order = createOrder();
        Product product = createProduct();
        product.deactivate();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.addItem(product, 1)
        );
    }

    @Test
    void shouldRejectZeroOrNegativeQuantity() {
        Order order = createOrder();
        Product product = createProduct();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.addItem(product, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> order.addItem(product, -1)
        );
    }

    @Test
    void shouldCalculateItemSubtotalAndOrderTotal() {
        Order order = createOrder();
        Product product = createProduct();

        order.addItem(product, 3);

        OrderItem item = order.getItems().getFirst();

        assertEquals(0, item.getUnitPrice().compareTo(
                new BigDecimal("10.00")
        ));

        assertEquals(3, item.getQuantity());

        assertEquals(0, item.getSubtotal().compareTo(
                new BigDecimal("30.00")
        ));

        assertEquals(0, order.getTotal().compareTo(
                new BigDecimal("30.00")
        ));
    }

    @Test
    void shouldRejectAddingSameProductTwice() {
        Order order = createOrder();
        Product product = createProduct();

        order.addItem(product, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> order.addItem(product, 1)
        );

        assertEquals(1, order.getItems().size());
        assertEquals(0, order.getTotal().compareTo(
                new BigDecimal("20.00")
        ));
    }

    @Test
    void shouldPreserveProductSnapshotWhenProductIsUpdated() {
        Order order = createOrder();
        Product product = createProduct();

        order.addItem(product, 2);

        product.updateDetails(
                "Cupcake de Chocolate Premium",
                "Nova descrição",
                new BigDecimal("15.00"),
                null
        );

        OrderItem item = order.getItems().getFirst();

        assertEquals("Cupcake de Chocolate", item.getProductName());

        assertEquals(0, item.getUnitPrice().compareTo(
                new BigDecimal("10.00")
        ));

        assertEquals(0, item.getSubtotal().compareTo(
                new BigDecimal("20.00")
        ));

        assertEquals(0, order.getTotal().compareTo(
                new BigDecimal("20.00")
        ));
    }

    private Order createOrder() {
        return new Order(
                "Maria Silva",
                "maria@example.com",
                "Rua das Flores, 100"
        );
    }

    private Product createProduct() {
        return new Product(
                "Cupcake de Chocolate",
                "Cupcake com cobertura de chocolate",
                new BigDecimal("10.00"),
                null
        );
    }
}