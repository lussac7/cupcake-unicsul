/**
 * Verifica se um valor é uma string não vazia.
 *
 * @param {*} value
 * @returns {boolean}
 */
export function isNonEmptyString(value) {
    return typeof value === "string" && value.trim().length > 0;
}

/**
 * Verifica se um endereço de e-mail possui formato válido.
 *
 * Esta validação verifica apenas a estrutura do endereço.
 * Não confirma se a caixa postal realmente existe.
 *
 * @param {*} value
 * @returns {boolean}
 */
export function isValidEmail(value) {
    if (typeof value !== "string") {
        return false;
    }

    const email = value.trim();

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

/**
 * Verifica se um valor representa um inteiro positivo.
 *
 * @param {*} value
 * @returns {boolean}
 */
export function isPositiveInteger(value) {
    return Number.isSafeInteger(value) && value > 0;
}