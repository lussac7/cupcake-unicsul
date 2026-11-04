import { formatCurrency } from "../utils/formatters.js";
import { addItem } from "../services/cart-service.js";

/**
 * Cria um cartão HTML para um produto.
 *
 * @param {Object} product - Dados do produto.
 * @returns {HTMLElement}
 */
export function createProductCard(product) {
    if (!product || !Number.isSafeInteger(product.id) || product.id <= 0) {
        throw new TypeError("A valid product is required.");
    }

    const card = document.createElement("article");
    card.className = "product-card";

    const image = document.createElement("img");
    image.src = product.imageUrl || "./assets/images/cupcake-placeholder.svg";
    image.alt = product.name;
    image.loading = "lazy";

    const content = document.createElement("div");
    content.className = "product-card-content";

    const title = document.createElement("h3");
    title.textContent = product.name;

    const description = document.createElement("p");
    description.textContent = product.description || "";

    const price = document.createElement("p");
    price.className = "product-card-price";
    price.textContent = formatCurrency(product.price);

    const actions = document.createElement("div");
    actions.className = "product-card-actions";

    const detailsLink = document.createElement("a");
    detailsLink.className = "btn btn-secondary";
    detailsLink.href = `#/products/${product.id}`;
    detailsLink.textContent = "Ver detalhes";

    const addButton = document.createElement("button");
    addButton.type = "button";
    addButton.className = "btn btn-primary";
    addButton.textContent = "Adicionar";

    addButton.addEventListener("click", () => {
        addItem(product.id);
        addButton.textContent = "Adicionado!";
    });

    actions.append(detailsLink, addButton);
    content.append(title, description, price, actions);
    card.append(image, content);

    return card;
}