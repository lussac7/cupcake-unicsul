package io.github.lussac7.cupcake.order.service;

import io.github.lussac7.cupcake.order.dto.OrderCreateRequest;
import io.github.lussac7.cupcake.order.dto.OrderItemRequest;
import io.github.lussac7.cupcake.order.dto.OrderItemResponse;
import io.github.lussac7.cupcake.order.dto.OrderResponse;
import io.github.lussac7.cupcake.order.entity.Order;
import io.github.lussac7.cupcake.order.entity.OrderItem;
import io.github.lussac7.cupcake.order.repository.OrderRepository;
import io.github.lussac7.cupcake.product.entity.Product;
import io.github.lussac7.cupcake.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse create(OrderCreateRequest request) {

        Map<Long, Integer> consolidatedItems = new LinkedHashMap<>();

        for (OrderItemRequest item : request.items()) {
            consolidatedItems.merge(
                    item.productId(),
                    item.quantity(),
                    Integer::sum
            );
        }

        Set<Long> requestedIds = consolidatedItems.keySet();

        List<Product> products =
                productRepository.findAllById(requestedIds);

        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        product -> product
                ));

        if (productsById.size() != requestedIds.size()) {
            throw new NoSuchElementException(
                    "One or more products were not found."
            );
        }

        Order order = new Order(
                request.customerName(),
                request.customerEmail(),
                request.deliveryAddress()
        );

        for (Map.Entry<Long, Integer> entry :
                consolidatedItems.entrySet()) {

            Product product = productsById.get(entry.getKey());

            if (!product.isActive()) {
                throw new IllegalArgumentException(
                        "Inactive products cannot be ordered: "
                                + product.getId()
                );
            }

            order.addItem(product, entry.getValue());
        }

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    public OrderResponse findById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Order not found with id: " + id
                ));

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getDeliveryAddress(),
                order.getCreatedAt(),
                order.getTotal(),
                items
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}