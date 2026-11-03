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

    @Test
    void shouldRejectNullProductPrice() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Cupcake de Chocolate",
                "Cupcake com cobertura de chocolate.",
                null,
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectProductPriceWithMoreThanTwoDecimalPlaces() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Cupcake de Chocolate",
                "Cupcake com cobertura de chocolate.",
                new BigDecimal("12.999"),
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectProductNameExceedingMaximumLength() {
        ProductCreateRequest request = new ProductCreateRequest(
                "A".repeat(151),
                "Descrição válida.",
                new BigDecimal("12.50"),
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectImageUrlExceedingMaximumLength() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Cupcake de Chocolate",
                "Descrição válida.",
                new BigDecimal("12.50"),
                "https://example.com/" + "a".repeat(500)
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectCustomerNameExceedingMaximumLength() {
        OrderCreateRequest request = new OrderCreateRequest(
                "A".repeat(151),
                "joao@example.com",
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(1L, 1))
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectCustomerEmailExceedingMaximumLength() {
        String email = "a".repeat(243) + "@example.com";

        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                email,
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(1L, 1))
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectNonPositiveProductId() {
        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                "joao@example.com",
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(0L, 1))
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectNegativeItemQuantity() {
        OrderCreateRequest request = new OrderCreateRequest(
                "João Silva",
                "joao@example.com",
                "Rua das Flores, 100",
                List.of(new OrderItemRequest(1L, -1))
        );

        assertFalse(validator.validate(request).isEmpty());
    }
}