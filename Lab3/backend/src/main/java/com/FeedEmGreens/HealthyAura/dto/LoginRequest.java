package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing a login request payload.
 *
 * <p>This class carries user credentials (username and password)
 * from the frontend to the backend during the authentication process.</p>
 *
 * <p>It is consumed by the {@link com.FeedEmGreens.HealthyAura.controller.loginController}
 * and handled by the {@link com.FeedEmGreens.HealthyAura.service.AuthService} to
 * authenticate users and generate JWT tokens.</p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "username": "johnDoe",
 *   "password": "securePassword123"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.dto.AuthResponse
 * @see com.FeedEmGreens.HealthyAura.dto.SignupRequest
 * @see com.FeedEmGreens.HealthyAura.service.AuthService
 * @see com.FeedEmGreens.HealthyAura.controller.loginController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class LoginRequest {

    /** The username of the user attempting to log in. */
    private String username;

    /** The password associated with the username. */
    private String password;

    /**
     * Retrieves the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the username entered during login.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's password.
     *
     * @param password the password entered by the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the username for this login request.
     *
     * @param username the username entered by the user
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
