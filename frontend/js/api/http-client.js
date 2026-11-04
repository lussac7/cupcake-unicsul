/**
 * Cupcake - HTTP Client
 *
 * Centralizes HTTP communication with the Spring Boot REST API.
 */

const API_BASE_URL = "http://localhost:8080";

const DEFAULT_HEADERS = {
    Accept: "application/json"
};


/**
 * Represents an unsuccessful HTTP response.
 */
export class HttpError extends Error {

    constructor(status, message, details = null) {
        super(message);

        this.name = "HttpError";
        this.status = status;
        this.details = details;
    }

}


/**
 * Builds the complete API URL.
 *
 * @param {string} path
 * @returns {string}
 */
function buildUrl(path) {
    return `${API_BASE_URL}${path}`;
}


/**
 * Extracts an error message from the response body.
 *
 * Supports the standard ProblemDetail response format.
 *
 * @param {object|null} body
 * @param {number} status
 * @returns {string}
 */
function getErrorMessage(body, status) {
    if (body && typeof body.detail === "string" && body.detail.trim()) {
        return body.detail;
    }

    if (body && typeof body.title === "string" && body.title.trim()) {
        return body.title;
    }

    return `HTTP request failed with status ${status}.`;
}


/**
 * Parses the HTTP response.
 *
 * @param {Response} response
 * @returns {Promise<unknown>}
 * @throws {HttpError}
 */
async function parseResponse(response) {
    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("Content-Type") ?? "";

    let body = null;

    if (contentType.includes("application/json")) {
        body = await response.json();
    } else {
        const text = await response.text();
        body = text || null;
    }

    if (!response.ok) {
        const message = getErrorMessage(body, response.status);

        throw new HttpError(
            response.status,
            message,
            body
        );
    }

    return body;
}


/**
 * Executes an HTTP request.
 *
 * @param {string} method
 * @param {string} path
 * @param {object|null} body
 * @returns {Promise<unknown>}
 */
async function request(method, path, body = null) {
    const options = {
        method,
        headers: {
            ...DEFAULT_HEADERS
        }
    };

    if (body !== null) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }

    let response;

    try {
        response = await fetch(buildUrl(path), options);
    } catch (error) {
        if (error instanceof Error) {
            throw error;
        }

        throw new Error("Unable to communicate with the server.");
    }

    return parseResponse(response);
}


/**
 * Sends an HTTP GET request.
 *
 * @param {string} path
 * @returns {Promise<unknown>}
 */
export async function get(path) {
    return request("GET", path);
}


/**
 * Sends an HTTP POST request with a JSON body.
 *
 * @param {string} path
 * @param {object} body
 * @returns {Promise<unknown>}
 */
export async function post(path, body) {
    return request("POST", path, body);
}


/**
 * Sends an HTTP PUT request with a JSON body.
 *
 * @param {string} path
 * @param {object} body
 * @returns {Promise<unknown>}
 */
export async function put(path, body) {
    return request("PUT", path, body);
}


/**
 * Sends an HTTP DELETE request.
 *
 * @param {string} path
 * @returns {Promise<unknown>}
 */
export async function deleteResource(path) {
    return request("DELETE", path);
}