package io.github.lussac7.cupcake.product.dto;

import java.math.BigDecimal;

public record ProductResponse(

        Long id,

        String name,

        String description,

        BigDecimal price,

        String imageUrl,

        boolean active

) {
}