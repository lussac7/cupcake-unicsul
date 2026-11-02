package io.github.lussac7.cupcake.dto;

import io.github.lussac7.cupcake.order.dto.OrderCreateRequest;
import io.github.lussac7.cupcake.order.dto.OrderItemRequest;
import io.github.lussac7.cupcake.product.dto.ProductCreateRequest;
import io.github.lussac7.cupcake.product.dto.ProductUpdateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldAcceptValidProductCreateRequest() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Cupcake de Chocolate",
                "Cupcake com cobertura de chocolate.",
                new BigDecimal("12.50"),
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectInvalidProductCreateRequest() {
        ProductCreateRequest request = new ProductCreateRequest(
                "",
                "",
                BigDecimal.ZERO,
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldAcceptValidProductUpdateRequest() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                "Cupcake de Morango",
                "Cupcake com recheio de morango.",
                new BigDecimal("15.00"),
                "https://example.com/cupcake.jpg"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectInvalidProductUpdateRequest() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                " ",
                "Descrição válida.",
                new BigDecimal("-5.00"),
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldAcceptValidOrderCreateRequest() {
        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                "joao@example.com",
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(1L, 2))
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectOrderWithoutItems() {
        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                "joao@example.com",
                "Rua das Flores, 100",
                List.of()
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectInvalidNestedOrderItem() {
        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                "joao@example.com",
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(null, 0))
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectInvalidCustomerEmail() {
        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                "email-invalido",
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(1L, 1))
        );

        assertFalse(validator.validate(request).isEmpty());
    }
}