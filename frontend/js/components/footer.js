/**
 * Renderiza o rodapé da aplicação.
 *
 * @param {HTMLElement} container - Elemento onde o rodapé será renderizado.
 */
export function renderFooter(container) {
    if (!(container instanceof HTMLElement)) {
        throw new TypeError("A valid HTML container is required.");
    }

    const currentYear = new Date().getFullYear();

    container.innerHTML = `
        <div class="container">
            <p>
                &copy; ${currentYear} Cupcake Unicsul.
                Todos os direitos reservados.
            </p>
            <p class="text-muted">
                Projeto acadêmico — UNICSUL.
            </p>
        </div>
    `;
}