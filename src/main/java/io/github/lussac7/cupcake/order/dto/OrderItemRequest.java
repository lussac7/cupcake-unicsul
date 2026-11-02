package io.github.lussac7.cupcake.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(

        @NotNull(message = "O identificador do produto é obrigatório.")
        @Positive(message = "O identificador do produto deve ser positivo.")
        Long productId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantity

) {
}