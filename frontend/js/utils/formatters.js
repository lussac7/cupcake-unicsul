/**
 * Formata um valor numérico como moeda brasileira.
 *
 * @param {number} value - Valor monetário.
 * @returns {string}
 */
export function formatCurrency(value) {
    if (typeof value !== "number" || !Number.isFinite(value)) {
        throw new TypeError("Currency value must be a finite number.");
    }

    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL"
    }).format(value);
}

/**
 * Formata uma data e hora no padrão brasileiro.
 *
 * @param {string|Date} value - Data a ser formatada.
 * @returns {string}
 */
export function formatDateTime(value) {
    const date = value instanceof Date ? value : new Date(value);

    if (Number.isNaN(date.getTime())) {
        throw new TypeError("Invalid date value.");
    }

    return new Intl.DateTimeFormat("pt-BR", {
        dateStyle: "short",
        timeStyle: "short"
    }).format(date);
}