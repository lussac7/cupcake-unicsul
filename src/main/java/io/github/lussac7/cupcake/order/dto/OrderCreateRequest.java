package io.github.lussac7.cupcake.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(

        @NotBlank(message = "O nome do cliente é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String customerName,

        @NotBlank(message = "O e-mail do cliente é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
        String customerEmail,

        @NotBlank(message = "O endereço de entrega é obrigatório.")
        String deliveryAddress,

        @NotEmpty(message = "O pedido deve conter pelo menos um produto.")
        List<@Valid OrderItemRequest> items

) {
}