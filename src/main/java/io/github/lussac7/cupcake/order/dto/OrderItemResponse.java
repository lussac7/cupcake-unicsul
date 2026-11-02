package io.github.lussac7.cupcake.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long productId,

        String productName,

        BigDecimal unitPrice,

        Integer quantity,

        BigDecimal subtotal

) {
}