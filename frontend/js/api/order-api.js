import { get, post } from "./http-client.js";

/**
 * Cria um pedido.
 *
 * @param {Object} orderData - Dados necessários para criar o pedido.
 * @returns {Promise<Object>} Pedido criado.
 */
export async function createOrder(orderData) {
    if (!orderData || typeof orderData !== "object" || Array.isArray(orderData)) {
        throw new TypeError("Order data must be an object.");
    }

    return post("/api/v1/orders", orderData);
}

/**
 * Retorna um pedido pelo seu identificador.
 *
 * @param {number|string} id - Identificador do pedido.
 * @returns {Promise<Object>} Pedido encontrado.
 */
export async function getOrderById(id) {
    const orderId = Number(id);

    if (!Number.isSafeInteger(orderId) || orderId <= 0) {
        throw new TypeError("Order ID must be a positive integer.");
    }

    return get(`/api/v1/orders/${orderId}`);
}