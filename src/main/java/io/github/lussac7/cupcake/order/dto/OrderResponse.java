package io.github.lussac7.cupcake.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(

        Long id,

        String customerName,

        String customerEmail,

        String deliveryAddress,

        Instant createdAt,

        BigDecimal total,

        List<OrderItemResponse> items

) {
    public OrderResponse {
        items = List.copyOf(items);
    }
}