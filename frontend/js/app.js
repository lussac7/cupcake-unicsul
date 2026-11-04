import { renderHeader } from "./components/header.js";
import { renderFooter } from "./components/footer.js";

const APP_CONTENT_ID = "app-content";
const DEFAULT_ROUTE = "/products";

/**
 * Configuração das rotas da aplicação.
 *
 * Cada rota utiliza carregamento dinâmico do respectivo módulo.
 */
const routes = [
    {
        pattern: /^\/products$/,
        load: () => import("./pages/catalog-page.js"),
        params: []
    },
    {
        pattern: /^\/products\/(\d+)$/,
        load: () => import("./pages/product-detail-page.js"),
        params: ["id"]
    },
    {
        pattern: /^\/cart$/,
        load: () => import("./pages/cart-page.js"),
        params: []
    },
    {
        pattern: /^\/checkout$/,
        load: () => import("./pages/checkout-page.js"),
        params: []
    },
    {
        pattern: /^\/orders\/(\d+)$/,
        load: () => import("./pages/order-page.js"),
        params: ["id"]
    }
];

/**
 * Retorna o elemento principal da aplicação.
 *
 * @returns {HTMLElement}
 */
function getAppContent() {
    const container = document.getElementById(APP_CONTENT_ID);

    if (!container) {
        throw new Error(
            `Application container #${APP_CONTENT_ID} not found.`
        );
    }

    return container;
}

/**
 * Obtém a rota atual a partir do fragmento da URL.
 *
 * @returns {string}
 */
function getCurrentRoute() {
    return window.location.hash.slice(1) || DEFAULT_ROUTE;
}

/**
 * Identifica a rota correspondente à URL atual.
 *
 * @param {string} route
 * @returns {Object|null}
 */
function matchRoute(route) {
    for (const routeConfig of routes) {
        const match = route.match(routeConfig.pattern);

        if (match) {
            return {
                load: routeConfig.load,
                params: match.slice(1)
            };
        }
    }

    return null;
}

/**
 * Apresenta uma página de rota não encontrada.
 *
 * @param {HTMLElement} container
 */
function renderNotFound(container) {
    container.innerHTML = `
        <section class="container">
            <h1>Página não encontrada</h1>
            <p>O endereço solicitado não existe.</p>
            <a href="#/products" class="btn btn-primary">
                Voltar ao catálogo
            </a>
        </section>
    `;
}

/**
 * Apresenta uma mensagem de erro de aplicação.
 *
 * @param {HTMLElement} container
 */
function renderApplicationError(container) {
    container.innerHTML = `
        <section class="container">
            <h1>Ops! Algo deu errado.</h1>
            <p>
                Não foi possível carregar esta página.
                Tente novamente.
            </p>
            <button
                type="button"
                class="btn btn-primary"
                id="retry-route"
            >
                Tentar novamente
            </button>
        </section>
    `;

    const retryButton = container.querySelector("#retry-route");

    retryButton.addEventListener("click", () => {
        renderRoute();
    });
}

/**
 * Carrega e renderiza a página correspondente à rota atual.
 */
async function renderRoute() {
    const container = getAppContent();
    const route = getCurrentRoute();
    const matchedRoute = matchRoute(route);

    if (!matchedRoute) {
        renderNotFound(container);
        return;
    }

    try {
        const pageModule = await matchedRoute.load();

        await pageModule.render(container, ...matchedRoute.params);

    } catch (error) {
        console.error("Application rendering error:", error);
        renderApplicationError(container);
    }
}

/**
 * Inicializa os componentes e o roteamento da aplicação.
 */
function initializeApp() {
    const headerContainer = document.getElementById("app-header");
    const footerContainer = document.getElementById("app-footer");

    renderHeader(headerContainer);
    renderFooter(footerContainer);

    window.addEventListener("hashchange", renderRoute);

    if (!window.location.hash) {
        window.location.hash = DEFAULT_ROUTE;
        return;
    }

    renderRoute();
}

/**
 * Inicializa a aplicação após o carregamento do DOM.
 */
document.addEventListener("DOMContentLoaded", initializeApp);