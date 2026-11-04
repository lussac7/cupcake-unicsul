import { get } from "./http-client.js";

/**
 * Retorna os produtos paginados.
 *
 * @param {Object} params - Parâmetros de paginação e ordenação.
 * @param {number} params.page - Página (base zero).
 * @param {number} params.size - Quantidade de registros por página.
 * @param {string} params.sort - Campo utilizado na ordenação.
 * @param {string} params.direction - Direção: ASC ou DESC.
 * @returns {Promise<Object>} Página de produtos.
 */
export async function getProducts({
                                      page = 0,
                                      size = 20,
                                      sort = "price",
                                      direction = "ASC"
                                  } = {}) {
    if (!Number.isInteger(page) || page < 0) {
        throw new TypeError("Page must be a non-negative integer.");
    }

    if (!Number.isInteger(size) || size <= 0) {
        throw new TypeError("Size must be a positive integer.");
    }

    if (typeof sort !== "string" || sort.trim() === "") {
        throw new TypeError("Sort must be a non-empty string.");
    }

    const normalizedDirection = direction.toUpperCase();

    if (!["ASC", "DESC"].includes(normalizedDirection)) {
        throw new TypeError("Direction must be ASC or DESC.");
    }

    const queryParams = new URLSearchParams({
        page: String(page),
        size: String(size),
        sort: sort.trim(),
        direction: normalizedDirection
    });

    return get(`/api/v1/products?${queryParams.toString()}`);
}

/**
 * Retorna um produto pelo seu identificador.
 *
 * @param {number|string} id - Identificador do produto.
 * @returns {Promise<Object>} Produto encontrado.
 */
export async function getProductById(id) {
    const productId = Number(id);

    if (!Number.isSafeInteger(productId) || productId <= 0) {
        throw new TypeError("Product ID must be a positive integer.");
    }

    return get(`/api/v1/products/${productId}`);
}