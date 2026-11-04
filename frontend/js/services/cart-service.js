const CART_STORAGE_KEY = "cupcake.cart";

/**
 * Valida um identificador de produto.
 *
 * @param {number|string} productId
 * @returns {number}
 */
function validateProductId(productId) {
    const id = Number(productId);

    if (!Number.isSafeInteger(id) || id <= 0) {
        throw new TypeError("Product ID must be a positive integer.");
    }

    return id;
}

/**
 * Valida uma quantidade.
 *
 * @param {number} quantity
 */
function validateQuantity(quantity) {
    if (!Number.isSafeInteger(quantity) || quantity <= 0) {
        throw new TypeError("Quantity must be a positive integer.");
    }
}

/**
 * Recupera os itens armazenados no carrinho.
 *
 * @returns {Array<Object>}
 */
export function getCart() {
    const storedCart = localStorage.getItem(CART_STORAGE_KEY);

    if (!storedCart) {
        return [];
    }

    try {
        const items = JSON.parse(storedCart);

        if (!Array.isArray(items)) {
            return [];
        }

        return items.filter(item =>
            Number.isSafeInteger(item.productId) &&
            item.productId > 0 &&
            Number.isSafeInteger(item.quantity) &&
            item.quantity > 0
        );
    } catch {
        return [];
    }
}

/**
 * Persiste os itens do carrinho e notifica os componentes.
 *
 * @param {Array<Object>} items
 */
function saveCart(items) {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));

    window.dispatchEvent(new Event("cart:updated"));
}

/**
 * Adiciona um produto ao carrinho.
 * Se o produto já existir, incrementa sua quantidade.
 *
 * @param {number|string} productId
 * @param {number} quantity
 */
export function addItem(productId, quantity = 1) {
    const id = validateProductId(productId);
    validateQuantity(quantity);

    const cart = getCart();
    const existingItem = cart.find(item => item.productId === id);

    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        cart.push({
            productId: id,
            quantity
        });
    }

    saveCart(cart);
}

/**
 * Atualiza a quantidade de um produto no carrinho.
 *
 * @param {number|string} productId
 * @param {number} quantity
 */
export function updateItemQuantity(productId, quantity) {
    const id = validateProductId(productId);
    validateQuantity(quantity);

    const cart = getCart();
    const item = cart.find(entry => entry.productId === id);

    if (!item) {
        throw new Error("Product not found in cart.");
    }

    item.quantity = quantity;
    saveCart(cart);
}

/**
 * Remove um produto do carrinho.
 *
 * @param {number|string} productId
 */
export function removeItem(productId) {
    const id = validateProductId(productId);
    const cart = getCart();

    saveCart(cart.filter(item => item.productId !== id));
}

/**
 * Remove todos os produtos do carrinho.
 */
export function clearCart() {
    localStorage.removeItem(CART_STORAGE_KEY);

    window.dispatchEvent(new Event("cart:updated"));
}

/**
 * Retorna a quantidade total de unidades no carrinho.
 *
 * @returns {number}
 */
export function getCartItemCount() {
    return getCart().reduce(
        (total, item) => total + item.quantity,
        0
    );
}