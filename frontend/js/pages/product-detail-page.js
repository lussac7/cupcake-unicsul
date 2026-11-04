import { getProductById } from "../api/product-api.js";
import { addItem } from "../services/cart-service.js";
import { formatCurrency } from "../utils/formatters.js";

/**
 * Renderiza a página de detalhes de um produto.
 *
 * @param {HTMLElement} container
 * @param {string|number} id - Identificador do produto.
 */
export async function render(container, id) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    container.innerHTML = `
        <section class="container">
            <a href="#/products">← Voltar ao catálogo</a>

            <div id="product-detail-status" role="status">
                Carregando produto...
            </div>

            <div id="product-detail-content"></div>
        </section>
    `;

    const statusElement = container.querySelector(
        "#product-detail-status"
    );

    const contentElement = container.querySelector(
        "#product-detail-content"
    );

    try {
        const product = await getProductById(id);

        if (!product || product.active !== true) {
            statusElement.textContent = "Produto não encontrado.";
            return;
        }

        statusElement.textContent = "";

        const article = document.createElement("article");
        article.className = "product-detail";

        const image = document.createElement("img");
        image.src = product.imageUrl || "./assets/images/cupcake-placeholder.svg";
        image.alt = product.name;
        image.loading = "lazy";

        const details = document.createElement("div");
        details.className = "product-detail-content";

        const title = document.createElement("h1");
        title.textContent = product.name;

        const description = document.createElement("p");
        description.textContent = product.description || "";

        const price = document.createElement("p");
        price.className = "product-card-price";
        price.textContent = formatCurrency(product.price);

        const addButton = document.createElement("button");
        addButton.type = "button";
        addButton.className = "btn btn-primary";
        addButton.textContent = "Adicionar ao carrinho";

        addButton.addEventListener("click", () => {
            addItem(product.id);
            addButton.textContent = "Adicionado ao carrinho!";
        });

        details.append(title, description, price, addButton);
        article.append(image, details);
        contentElement.append(article);

    } catch (error) {
        console.error("Error loading product:", error);

        statusElement.textContent =
            "Não foi possível carregar o produto.";

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