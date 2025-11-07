package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing the response returned after
 * a successful authentication (login or signup) operation.
 *
 * <p>This object contains the essential authentication details required
 * for frontend clients to establish a secure session, including:</p>
 * <ul>
 *     <li>A JWT token for authorization in subsequent requests</li>
 *     <li>The authenticated user's username</li>
 *     <li>The user's assigned role (e.g., USER, ADMIN)</li>
 * </ul>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "username": "johnDoe",
 *   "role": "USER"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.dto.LoginRequest
 * @see com.FeedEmGreens.HealthyAura.dto.SignupRequest
 * @see com.FeedEmGreens.HealthyAura.service.AuthService
 *
 * @author Rusha
 * @version 1.0
 * @since 2025-11-07
 */
public class AuthResponse {

    /** The JWT token generated after successful authentication. */
    private String token;

    /** The username of the authenticated user. */
    private String username;

    /** The role assigned to the authenticated user (e.g., USER or ADMIN). */
    private String role;

    /**
     * Constructs an {@code AuthResponse} with the given token, username, and role.
     *
     * @param token    the JWT token for authorization
     * @param username the username of the authenticated user
     * @param role     the role assigned to the user
     */
    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    /**
     * Returns the JWT token associated with this response.
     *
     * @return the authentication token
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the username of the authenticated user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the role assigned to the authenticated user.
     *
     * @return the user role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the JWT token value.
     *
     * @param token the new token to assign
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Sets the username value.
     *
     * @param username the username to assign
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the user role value.
     *
     * @param role the role to assign
     */
    public void setRole(String role) {
        this.role = role;
    }
}
