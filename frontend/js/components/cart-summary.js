import { formatCurrency } from "../utils/formatters.js";

/**
 * Renderiza o resumo financeiro do carrinho.
 *
 * @param {HTMLElement} container - Elemento onde o resumo será renderizado.
 * @param {Array<Object>} items - Produtos e respectivas quantidades.
 */
export function renderCartSummary(container, items) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    if (!Array.isArray(items)) {
        throw new TypeError("Cart items must be an array.");
    }

    const totalQuantity = items.reduce(
        (total, item) => total + item.quantity,
        0
    );

    const totalAmount = items.reduce(
        (total, item) => total + item.product.price * item.quantity,
        0
    );

    const summary = document.createElement("section");
    summary.className = "cart-summary";

    const title = document.createElement("h2");
    title.textContent = "Resumo do pedido";

    const quantity = document.createElement("p");
    quantity.textContent = `Quantidade de unidades: ${totalQuantity}`;

    const total = document.createElement("p");
    total.className = "cart-summary-total";
    total.textContent = `Total: ${formatCurrency(totalAmount)}`;

    summary.append(title, quantity, total);

    container.replaceChildren(summary);
}