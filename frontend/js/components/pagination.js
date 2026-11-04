/**
 * Renderiza os controles de paginação.
 *
 * @param {HTMLElement} container - Elemento onde a paginação será renderizada.
 * @param {Object} page - Objeto Page retornado pelo backend.
 * @param {Function} onPageChange - Callback chamado com o número da página.
 */
export function renderPagination(container, page, onPageChange) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    if (
        !page ||
        !Number.isInteger(page.number) ||
        !Number.isInteger(page.totalPages) ||
        page.number < 0 ||
        page.totalPages < 0
    ) {
        throw new TypeError("Invalid pagination data.");
    }

    if (typeof onPageChange !== "function") {
        throw new TypeError("A valid page change callback is required.");
    }

    container.replaceChildren();

    if (page.totalPages <= 1) {
        return;
    }

    const navigation = document.createElement("nav");
    navigation.className = "pagination";
    navigation.setAttribute("aria-label", "Navegação entre páginas");

    const previousButton = document.createElement("button");
    previousButton.type = "button";
    previousButton.className = "btn btn-secondary";
    previousButton.textContent = "Anterior";
    previousButton.disabled = page.number === 0;

    previousButton.addEventListener("click", () => {
        onPageChange(page.number - 1);
    });

    const pageIndicator = document.createElement("span");
    pageIndicator.className = "pagination-info";
    pageIndicator.textContent =
        `Página ${page.number + 1} de ${page.totalPages}`;

    const nextButton = document.createElement("button");
    nextButton.type = "button";
    nextButton.className = "btn btn-secondary";
    nextButton.textContent = "Próxima";
    nextButton.disabled = page.number >= page.totalPages - 1;

    nextButton.addEventListener("click", () => {
        onPageChange(page.number + 1);
    });

    navigation.append(
        previousButton,
        pageIndicator,
        nextButton
    );

    container.append(navigation);
}