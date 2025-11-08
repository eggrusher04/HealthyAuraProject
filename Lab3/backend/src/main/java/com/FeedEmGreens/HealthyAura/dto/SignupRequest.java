package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) used for user registration requests.
 *
 * <p>This class encapsulates the required information for creating a new user
 * or admin account in the system. It is typically consumed by the
 * {@link com.FeedEmGreens.HealthyAura.controller.loginController}
 * through endpoints such as:
 * <ul>
 *   <li><code>/auth/signup</code> — for regular user registration</li>
 *   <li><code>/auth/admin/signup</code> — for admin account creation</li>
 * </ul>
 * </p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "email": "john@example.com",
 *   "username": "johnDoe",
 *   "password": "securePassword123",
 *   "role": "USER"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.service.AuthService
 * @see com.FeedEmGreens.HealthyAura.controller.loginController
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class SignupRequest {

    /** The email address of the new user. */
    private String email;

    /** The username chosen by the user during registration. */
    private String username;

    /** The password chosen by the user (to be encrypted before storage). */
    private String password;

    /** The role of the user (e.g., "USER" or "ADMIN"). */
    private String role;

    /**
     * Sets the username of the user.
     *
     * @param username the desired username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password for the user.
     *
     * @param password the plain-text password (will be hashed before storage)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the role for the user.
     *
     * @param role the user's role (e.g., "USER" or "ADMIN")
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the username entered by the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password entered by the user.
     *
     * @return the password (plain-text before encryption)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the user's email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user role specified in the registration request.
     *
     * @return the role (e.g., "USER", "ADMIN")
     */
    public String getRole() {
        return role;
    }
}
