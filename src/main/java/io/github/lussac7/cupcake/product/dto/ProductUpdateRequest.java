package io.github.lussac7.cupcake.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(

        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String name,

        @NotBlank(message = "A descrição do produto é obrigatória.")
        String description,

        @NotNull(message = "O preço do produto é obrigatório.")
        @DecimalMin(
                value = "0.01",
                message = "O preço deve ser maior que zero."
        )
        @Digits(
                integer = 8,
                fraction = 2,
                message = "O preço deve ter no máximo 8 dígitos inteiros e 2 casas decimais."
        )
        BigDecimal price,

        @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres.")
        String imageUrl

) {
}