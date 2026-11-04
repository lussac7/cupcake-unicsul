import { getCartItemCount } from "../services/cart-service.js";

/**
 * Renderiza o cabeçalho da aplicação.
 *
 * @param {HTMLElement} container
 */
export function renderHeader(container) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    container.innerHTML = `
        <div class="header-content">
            <a
                class="header-logo"
                href="#/products"
                aria-label="Cupcake Unicsul - Página inicial"
            >
                Cupcake Unicsul
            </a>

            <nav class="header-nav" aria-label="Navegação principal">
                <a href="#/products">Produtos</a>

                <a href="#/cart">
                    Carrinho
                    <span
                        id="header-cart-count"
                        aria-live="polite"
                        aria-label="Quantidade de itens no carrinho"
                    >0</span>
                </a>
            </nav>
        </div>
    `;

    updateCartCount(container);

    window.addEventListener("cart:updated", () => {
        updateCartCount(container);
    });
}

/**
 * Atualiza o indicador de quantidade de itens no cabeçalho.
 *
 * @param {HTMLElement} container
 */
export function updateCartCount(container) {
    const countElement = container.querySelector("#header-cart-count");

    if (!countElement) {
        return;
    }

    countElement.textContent = String(getCartItemCount());
}