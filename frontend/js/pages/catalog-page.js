import { getProducts } from "../api/product-api.js";
import { createProductCard } from "../components/product-card.js";
import { renderPagination } from "../components/pagination.js";

const PAGE_SIZE = 20;

/**
 * Renderiza a página do catálogo.
 *
 * @param {HTMLElement} container
 */
export async function render(container) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    container.innerHTML = `
        <section class="container">
            <h1>Nosso catálogo</h1>
            <p class="text-muted">
                Conheça nossos deliciosos cupcakes.
            </p>

            <div id="catalog-status" role="status"></div>

            <div id="product-grid" class="product-grid"></div>

            <div id="catalog-pagination"></div>
        </section>
    `;

    await loadProducts(container, 0);
}

/**
 * Carrega e apresenta uma página de produtos.
 *
 * @param {HTMLElement} container
 * @param {number} pageNumber
 */
async function loadProducts(container, pageNumber) {
    const statusElement = container.querySelector("#catalog-status");
    const gridElement = container.querySelector("#product-grid");
    const paginationElement = container.querySelector("#catalog-pagination");

    statusElement.textContent = "Carregando produtos...";
    gridElement.replaceChildren();
    paginationElement.replaceChildren();

    try {
        const page = await getProducts({
            page: pageNumber,
            size: PAGE_SIZE,
            sort: "price",
            direction: "ASC"
        });

        if (!Array.isArray(page.content)) {
            throw new Error("Invalid product response from server.");
        }

        statusElement.textContent = "";

        if (page.content.length === 0) {
            statusElement.textContent = "Nenhum produto disponível.";
            return;
        }

        const fragment = document.createDocumentFragment();

        page.content.forEach(product => {
            fragment.append(createProductCard(product));
        });

        gridElement.append(fragment);

        renderPagination(
            paginationElement,
            page,
            selectedPage => loadProducts(container, selectedPage)
        );

    } catch (error) {
        console.error("Error loading products:", error);

        statusElement.textContent =
            "Não foi possível carregar os produtos. Tente novamente.";

        const retryButton = document.createElement("button");
        retryButton.type = "button";
        retryButton.className = "btn btn-primary";
        retryButton.textContent = "Tentar novamente";

        retryButton.addEventListener("click", () => {
            loadProducts(container, pageNumber);
        });

        statusElement.append(document.createElement("br"), retryButton);
    }
}