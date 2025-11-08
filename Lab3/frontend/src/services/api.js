import axios from "axios";

/**
 * Axios API Configuration
 *
 * <p>This module centralizes all API communication for the HealthyAura frontend.
 * It defines a base URL and sets default headers for every HTTP request.
 * It also injects JWT tokens automatically into the request header for
 * authenticated API calls.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *   <li>Configures a reusable `axios` instance with JSON headers.</li>
 *   <li>Includes <code>withCredentials</code> for secure cookie exchange.</li>
 *   <li>Uses a request interceptor to attach <b>Bearer JWT tokens</b> from `localStorage`.</li>
 *   <li>Ensures all authorized routes (e.g. `/auth`, `/profile`, `/rewards`) include authentication headers.</li>
 * </ul>
 *
 * <p>Example Usage:</p>
 * <pre><code>
 * import API from "../services/api";
 *
 * // Example GET request
 * const res = await API.get("/profile/me");
 *
 * // Example POST request
 * const loginRes = await API.post("/auth/login", { email, password });
 * </code></pre>
 *
 * @module services/api
 * @returns {AxiosInstance} Configured axios instance for all HTTP requests.
 * @since 2025-11-07
 */

const API = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

/**
 * Axios Request Interceptor
 *
 * <p>Intercepts all outgoing requests and attaches the JWT token
 * from <code>localStorage</code> (if available) as an
 * <code>Authorization: Bearer &lt;token&gt;</code> header.</p>
 *
 * @param {import("axios").InternalAxiosRequestConfig} config - The outgoing Axios request configuration.
 * @returns {import("axios").InternalAxiosRequestConfig} Modified config with Authorization header if token exists.
 */

API.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

export default API;