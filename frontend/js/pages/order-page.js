import { getOrderById } from "../api/order-api.js";
import { formatCurrency, formatDateTime } from "../utils/formatters.js";

/**
 * Renderiza a página de detalhes do pedido.
 *
 * @param {HTMLElement} container
 * @param {string|number} id - Identificador do pedido.
 */
export async function render(container, id) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    container.innerHTML = `
        <section class="container">
            <h1>Detalhes do pedido</h1>

            <a href="#/products">Voltar ao catálogo</a>

            <div id="order-status" role="status">
                Carregando pedido...
            </div>

            <div id="order-content"></div>
        </section>
    `;

    const statusElement = container.querySelector("#order-status");
    const contentElement = container.querySelector("#order-content");

    try {
        const order = await getOrderById(id);

        if (!order) {
            statusElement.textContent = "Pedido não encontrado.";
            return;
        }

        statusElement.textContent = "";

        renderOrderDetails(contentElement, order);

    } catch (error) {
        console.error("Error loading order:", error);

        statusElement.textContent =
            "Não foi possível carregar o pedido.";

        const retryButton = document.createElement("button");
        retryButton.type = "button";
        retryButton.className = "btn btn-primary";
        retryButton.textContent = "Tentar novamente";

        retryButton.addEventListener("click", () => {
            render(container, id);
        });

        statusElement.append(
            document.createElement("br"),
            retryButton
        );
    }
}

/**
 * Apresenta os dados do pedido.
 *
 * @param {HTMLElement} container
 * @param {Object} order
 */
function renderOrderDetails(container, order) {
    const section = document.createElement("section");
    section.className = "order-details";

    const orderNumber = document.createElement("h2");
    orderNumber.textContent = `Pedido #${order.id}`;

    const customerName = document.createElement("p");
    customerName.textContent = `Cliente: ${order.customerName}`;

    const customerEmail = document.createElement("p");
    customerEmail.textContent = `E-mail: ${order.customerEmail}`;

    const deliveryAddress = document.createElement("p");
    deliveryAddress.textContent =
        `Endereço de entrega: ${order.deliveryAddress}`;

    const createdAt = document.createElement("p");
    createdAt.textContent =
        `Data do pedido: ${formatDateTime(order.createdAt)}`;

    const itemsHeading = document.createElement("h3");
    itemsHeading.textContent = "Produtos";

    const itemsList = document.createElement("ul");

    order.items.forEach(item => {
        const listItem = document.createElement("li");

        listItem.textContent =
            `${item.productName} — ` +
            `${item.quantity} x ${formatCurrency(item.unitPrice)} ` +
            `= ${formatCurrency(item.subtotal)}`;

        itemsList.append(listItem);
    });

    const total = document.createElement("p");
    total.className = "cart-summary-total";
    total.textContent = `Total do pedido: ${formatCurrency(order.total)}`;

    section.append(
        orderNumber,
        customerName,
        customerEmail,
        deliveryAddress,
        createdAt,
        itemsHeading,
        itemsList,
        total
    );

    container.append(section);
}