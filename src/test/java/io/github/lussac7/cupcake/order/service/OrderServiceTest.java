package io.github.lussac7.cupcake.order.service;

import io.github.lussac7.cupcake.order.dto.OrderCreateRequest;
import io.github.lussac7.cupcake.order.dto.OrderItemRequest;
import io.github.lussac7.cupcake.order.dto.OrderResponse;
import io.github.lussac7.cupcake.order.entity.Order;
import io.github.lussac7.cupcake.order.repository.OrderRepository;
import io.github.lussac7.cupcake.product.entity.Product;
import io.github.lussac7.cupcake.product.repository.ProductRepository;
import io.github.lussac7.cupcake.shared.exception.BusinessRuleException;
import io.github.lussac7.cupcake.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
        Product product = createProduct(
                1L,
                "Chocolate",
                "8.50",
                true
        );

        OrderCreateRequest request = createRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        when(productRepository.findAllById(anyIterable()))
                .thenReturn(List.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(request);

        assertEquals("Cliente Teste", response.customerName());
        assertEquals(1, response.items().size());
        assertEquals("Chocolate", response.items().getFirst().productName());
        assertEquals(2, response.items().getFirst().quantity());
        assertEquals(new BigDecimal("8.50"), response.items().getFirst().unitPrice());
        assertEquals(new BigDecimal("17.00"), response.total());

        verify(productRepository).findAllById(anyIterable());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldConsolidateRepeatedProducts() {
        Product product = createProduct(
                1L,
                "Chocolate",
                "8.50",
                true
        );

        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(1L, 3)
        ));

        when(productRepository.findAllById(anyIterable()))
                .thenReturn(List.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(request);

        assertEquals(1, response.items().size());
        assertEquals(5, response.items().getFirst().quantity());
        assertEquals(new BigDecimal("42.50"), response.total());
    }

    @Test
    void shouldRejectOrderWhenProductDoesNotExist() {
        OrderCreateRequest request = createRequest(
                List.of(new OrderItemRequest(99L, 1))
        );

        when(productRepository.findAllById(anyIterable()))
                .thenReturn(List.of());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.create(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldRejectOrderWhenProductIsInactive() {
        Product product = createProduct(
                1L,
                "Chocolate",
                "8.50",
                false
        );

        OrderCreateRequest request = createRequest(
                List.of(new OrderItemRequest(1L, 1))
        );

        when(productRepository.findAllById(anyIterable()))
                .thenReturn(List.of(product));

        assertThrows(
                BusinessRuleException.class,
                () -> orderService.create(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldUseDatabasePriceInsteadOfClientPrice() {
        Product product = createProduct(
                1L,
                "Chocolate",
                "8.50",
                true
        );

        OrderCreateRequest request = createRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        when(productRepository.findAllById(anyIterable()))
                .thenReturn(List.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.create(request);

        assertEquals(new BigDecimal("8.50"), response.items().getFirst().unitPrice());
        assertEquals(new BigDecimal("17.00"), response.total());
    }

    @Test
    void shouldFindOrderById() {
        Product product = createProduct(
                1L,
                "Chocolate",
                "8.50",
                true
        );

        Order order = new Order(
                "Cliente Teste",
                "cliente@email.com",
                "Rua das Flores, 100"
        );

        order.addItem(product, 2);

        when(orderRepository.findById(10L))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.findById(10L);

        assertEquals("Cliente Teste", response.customerName());
        assertEquals(1, response.items().size());
        assertEquals(new BigDecimal("17.00"), response.total());

        verify(orderRepository).findById(10L);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.findById(99L)
        );
    }

    private Product createProduct(
            Long id,
            String name,
            String price,
            boolean active
    ) {
        Product product = mock(Product.class);

        when(product.getId()).thenReturn(id);
        lenient().when(product.getName()).thenReturn(name);
        lenient().when(product.getPrice()).thenReturn(new BigDecimal(price));
        when(product.isActive()).thenReturn(active);

        return product;
    }

    private OrderCreateRequest createRequest(
            List<OrderItemRequest> items
    ) {
        return new OrderCreateRequest(
                "Cliente Teste",
                "cliente@email.com",
                "Rua das Flores, 100",
                items
        );
    }
}