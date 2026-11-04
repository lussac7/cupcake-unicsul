import { getProductById } from "../api/product-api.js";
import { HttpError } from "../api/http-client.js";

import {
    getCart,
    updateItemQuantity,
    removeItem,
    clearCart
} from "../services/cart-service.js";

import { formatCurrency } from "../utils/formatters.js";
import { isPositiveInteger } from "../utils/validators.js";
import { renderCartSummary } from "../components/cart-summary.js";

/**
 * Renderiza a página do carrinho.
 *
 * @param {HTMLElement} container
 */
export async function render(container) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    container.innerHTML = `
        <section class="container">
            <h1>Meu carrinho</h1>

            <div id="cart-status" role="status"></div>

            <div id="cart-items"></div>

            <div id="cart-summary"></div>

            <div id="cart-actions"></div>
        </section>
    `;

    await loadCart(container);
}

/**
 * Recupera os produtos e apresenta o carrinho.
 *
 * @param {HTMLElement} container
 */
async function loadCart(container) {
    const statusElement = container.querySelector("#cart-status");
    const itemsElement = container.querySelector("#cart-items");
    const summaryElement = container.querySelector("#cart-summary");
    const actionsElement = container.querySelector("#cart-actions");

    statusElement.textContent = "Carregando carrinho...";

    itemsElement.replaceChildren();
    summaryElement.replaceChildren();
    actionsElement.replaceChildren();

    const cart = getCart();

    if (cart.length === 0) {
        statusElement.textContent = "Seu carrinho está vazio.";
        return;
    }

    try {
        const results = await Promise.all(
            cart.map(async item => {
                try {
                    const product = await getProductById(item.productId);

                    if (!product || product.active !== true) {
                        return { item, product: null };
                    }

                    return { item, product };
                } catch (error) {
                    if (error instanceof HttpError && error.status === 404) {
                        return { item, product: null };
                    }

                    throw error;
                }
            })
        );

        statusElement.textContent = "";

        const availableItems = results.filter(
            result => result.product !== null
        );

        const unavailableItems = results.filter(
            result => result.product === null
        );

        availableItems.forEach(({ item, product }) => {
            renderCartItem(itemsElement, item, product, container);
        });

        unavailableItems.forEach(({ item }) => {
            renderUnavailableItem(itemsElement, item, container);
        });

        if (unavailableItems.length > 0) {
            statusElement.textContent =
                "Alguns produtos não estão mais disponíveis. " +
                "Remova-os antes de continuar para o checkout.";
        }

        if (availableItems.length > 0) {
            const summaryItems = availableItems.map(({ item, product }) => ({
                product,
                quantity: item.quantity
            }));

            renderCartSummary(summaryElement, summaryItems);
        }

        if (unavailableItems.length === 0 && availableItems.length > 0) {
            const checkoutLink = document.createElement("a");
            checkoutLink.href = "#/checkout";
            checkoutLink.className = "btn btn-primary";
            checkoutLink.textContent = "Continuar para checkout";

            actionsElement.append(checkoutLink);
        }

        if (cart.length > 0) {
            const clearButton = document.createElement("button");
            clearButton.type = "button";
            clearButton.className = "btn btn-secondary";
            clearButton.textContent = "Limpar carrinho";

            clearButton.addEventListener("click", () => {
                clearCart();
                loadCart(container);
            });

            actionsElement.append(clearButton);
        }

    } catch (error) {
        console.error("Error loading cart:", error);

        statusElement.textContent =
            "Não foi possível carregar o carrinho. Tente novamente.";

        const retryButton = document.createElement("button");
        retryButton.type = "button";
        retryButton.className = "btn btn-primary";
        retryButton.textContent = "Tentar novamente";

        retryButton.addEventListener("click", () => {
            loadCart(container);
        });

        statusElement.append(
            document.createElement("br"),
            retryButton
        );
    }
}

/**
 * Renderiza um produto disponível no carrinho.
 *
 * @param {HTMLElement} container
 * @param {Object} item
 * @param {Object} product
 * @param {HTMLElement} pageContainer
 */
function renderCartItem(container, item, product, pageContainer) {
    const article = document.createElement("article");
    article.className = "cart-item";

    const name = document.createElement("h2");
    name.textContent = product.name;

    const unitPrice = document.createElement("p");
    unitPrice.textContent = `Preço unitário: ${formatCurrency(product.price)}`;

    const subtotal = document.createElement("p");
    subtotal.textContent =
        `Subtotal: ${formatCurrency(product.price * item.quantity)}`;

    const quantityLabel = document.createElement("label");
    quantityLabel.textContent = "Quantidade: ";

    const quantityInput = document.createElement("input");
    quantityInput.type = "number";
    quantityInput.min = "1";
    quantityInput.step = "1";
    quantityInput.value = String(item.quantity);
    quantityInput.className = "form-control";

    quantityLabel.append(quantityInput);

    const updateButton = document.createElement("button");
    updateButton.type = "button";
    updateButton.className = "btn btn-secondary";
    updateButton.textContent = "Atualizar";

    updateButton.addEventListener("click", () => {
        const quantity = Number(quantityInput.value);

        if (!isPositiveInteger(quantity)) {
            quantityInput.setCustomValidity(
                "Informe uma quantidade inteira positiva."
            );
            quantityInput.reportValidity();
            return;
        }

        quantityInput.setCustomValidity("");

        updateItemQuantity(item.productId, quantity);
        loadCart(pageContainer);
    });

    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "btn btn-secondary";
    removeButton.textContent = "Remover";

    removeButton.addEventListener("click", () => {
        removeItem(item.productId);
        loadCart(pageContainer);
    });

    article.append(
        name,
        unitPrice,
        subtotal,
        quantityLabel,
        updateButton,
        removeButton
    );

    container.append(article);
}

/**
 * Apresenta um produto que não está mais disponível.
 *
 * @param {HTMLElement} container
 * @param {Object} item
 * @param {HTMLElement} pageContainer
 */
function renderUnavailableItem(container, item, pageContainer) {
    const article = document.createElement("article");
    article.className = "cart-item";

    const message = document.createElement("p");
    message.textContent =
        `Produto ${item.productId} não está mais disponível.`;

    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "btn btn-secondary";
    removeButton.textContent = "Remover do carrinho";

    removeButton.addEventListener("click", () => {
        removeItem(item.productId);
        loadCart(pageContainer);
    });

    article.append(message, removeButton);
    container.append(article);
}