import { getProductById } from "../api/product-api.js";
import { createOrder } from "../api/order-api.js";

import {
    getCart,
    clearCart
} from "../services/cart-service.js";

import { renderCartSummary } from "../components/cart-summary.js";
import {
    isNonEmptyString,
    isValidEmail
} from "../utils/validators.js";

/**
 * Renderiza a página de checkout.
 *
 * @param {HTMLElement} container
 */
export async function render(container) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    container.innerHTML = `
        <section class="container">
            <h1>Finalizar pedido</h1>

            <div id="checkout-status" role="status"></div>

            <div id="checkout-summary"></div>

            <form id="checkout-form">
                <div class="form-group">
                    <label class="form-label" for="customer-name">
                        Nome completo
                    </label>
                    <input
                        class="form-control"
                        id="customer-name"
                        name="customerName"
                        type="text"
                        maxlength="150"
                        required
                    >
                </div>

                <div class="form-group">
                    <label class="form-label" for="customer-email">
                        E-mail
                    </label>
                    <input
                        class="form-control"
                        id="customer-email"
                        name="customerEmail"
                        type="email"
                        maxlength="254"
                        required
                    >
                </div>

                <div class="form-group">
                    <label class="form-label" for="delivery-address">
                        Endereço de entrega
                    </label>
                    <textarea
                        class="form-control"
                        id="delivery-address"
                        name="deliveryAddress"
                        maxlength="500"
                        required
                    ></textarea>
                </div>

                <button
                    class="btn btn-primary"
                    id="submit-order"
                    type="submit"
                >
                    Confirmar pedido
                </button>
            </form>
        </section>
    `;

    await initializeCheckout(container);
}

/**
 * Carrega os produtos e inicializa o checkout.
 *
 * @param {HTMLElement} container
 */
async function initializeCheckout(container) {
    const statusElement = container.querySelector("#checkout-status");
    const summaryElement = container.querySelector("#checkout-summary");
    const form = container.querySelector("#checkout-form");

    const cart = getCart();

    if (cart.length === 0) {
        statusElement.textContent = "Seu carrinho está vazio.";
        form.remove();
        return;
    }

    statusElement.textContent = "Verificando produtos...";

    try {
        const results = await Promise.all(
            cart.map(async item => {
                const product = await getProductById(item.productId);

                if (!product || product.active !== true) {
                    throw new Error(
                        `O produto ${item.productId} não está disponível.`
                    );
                }

                return {
                    product,
                    quantity: item.quantity
                };
            })
        );

        statusElement.textContent = "";

        renderCartSummary(summaryElement, results);

        form.addEventListener("submit", event => {
            event.preventDefault();
            submitCheckout(form, results, container);
        });

    } catch (error) {
        console.error("Error initializing checkout:", error);

        statusElement.textContent =
            "Não foi possível validar os produtos do carrinho. " +
            "Volte ao carrinho e tente novamente.";

        form.remove();
    }
}

/**
 * Valida os dados e envia o pedido ao backend.
 *
 * @param {HTMLFormElement} form
 * @param {Array<Object>} items
 * @param {HTMLElement} container
 */
async function submitCheckout(form, items, container) {
    const statusElement = container.querySelector("#checkout-status");
    const submitButton = form.querySelector("#submit-order");

    const formData = new FormData(form);

    const customerName = formData.get("customerName").trim();
    const customerEmail = formData.get("customerEmail").trim();
    const deliveryAddress = formData.get("deliveryAddress").trim();

    if (
        !isNonEmptyString(customerName) ||
        !isNonEmptyString(customerEmail) ||
        !isValidEmail(customerEmail) ||
        !isNonEmptyString(deliveryAddress)
    ) {
        statusElement.textContent =
            "Verifique os dados informados antes de continuar.";
        return;
    }

    const orderData = {
        customerName,
        customerEmail,
        deliveryAddress,
        items: items.map(item => ({
            productId: item.product.id,
            quantity: item.quantity
        }))
    };

    submitButton.disabled = true;
    statusElement.textContent = "Enviando pedido...";

    try {
        const order = await createOrder(orderData);

        if (!order || !Number.isSafeInteger(order.id) || order.id <= 0) {
            throw new Error("Invalid order response from server.");
        }

        clearCart();

        form.remove();

        const confirmation = document.createElement("div");

        const message = document.createElement("p");
        message.textContent =
            `Pedido #${order.id} criado com sucesso!`;

        const orderLink = document.createElement("a");
        orderLink.href = `#/orders/${order.id}`;
        orderLink.className = "btn btn-primary";
        orderLink.textContent = "Consultar meu pedido";

        confirmation.append(message, orderLink);

        statusElement.replaceChildren(confirmation);

    } catch (error) {
        console.error("Error creating order:", error);

        statusElement.textContent =
            "Não foi possível confirmar o pedido. " +
            "Verifique sua conexão e tente novamente.";

        submitButton.disabled = false;
    }
}