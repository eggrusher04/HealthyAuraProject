package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.LoginRequest;
import com.FeedEmGreens.HealthyAura.dto.SignupRequest;
import com.FeedEmGreens.HealthyAura.dto.AuthResponse;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for user authentication and account creation.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *     <li>User registration (signup)</li>
 *     <li>Administrator account creation</li>
 *     <li>User login and JWT-based authentication</li>
 * </ul>
 *
 * <p>Controllers in Spring Boot are designed to be thin — delegating
 * business logic to the service layer ({@link AuthService}) to maintain
 * a clean separation of concerns.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

/* Due to the springboot framework, controllers are kept thin to handle HTTP requests */

@RestController
@RequestMapping("/auth")
public class loginController {

    /**
     * Service layer responsible for handling authentication,
     * account creation, and JWT token management.
     */
    @Autowired
    private AuthService authService;

    /**
     * Registers a new user account with a default <code>USER</code> role.
     *
     * <p>This endpoint accepts a {@link SignupRequest} payload containing
     * the user's email, username, and password. Upon successful registration,
     * a {@link Users} entity is returned.</p>
     *
     * <p>Usage example:</p>
     * <pre>
     * POST /auth/signup
     * {
     *   "email": "user@example.com",
     *   "username": "johnDoe",
     *   "password": "securePassword"
     * }
     * </pre>
     *
     * @param req the signup details containing email, username, and password
     * @return a {@link ResponseEntity} containing the created {@link Users} object
     */
    @PostMapping("/signup")
    public ResponseEntity<Users> signup(@RequestBody SignupRequest req){
        return ResponseEntity.ok(authService.signup(req.getEmail(), req.getUsername(), req.getPassword(), "USER"));
    }

    /**
     * Creates a new administrator account.
     *
     * <p>This endpoint is restricted to existing admins and requires authentication.
     * It allows privileged users to create new admin accounts with elevated permissions.</p>
     *
     * <p>Authorization: Requires role <code>ADMIN</code>.</p>
     *
     * @param req the signup details for the new admin (email, username, password)
     * @return a {@link ResponseEntity} containing the created admin {@link Users} object
     */
    //Ensure ONLY admin can create an account(admin has to log in first)
    @PostMapping("/admin/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> createAdmin(@RequestBody SignupRequest req){
        return ResponseEntity.ok(authService.createAdmin(req.getEmail(), req.getUsername(), req.getPassword(), "ADMIN"));
    }

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     *
     * <p>This endpoint validates user credentials provided in the {@link LoginRequest}.
     * If authentication is successful, an {@link AuthResponse} containing the JWT token
     * and user information is returned.</p>
     *
     * <p>Usage example:</p>
     * <pre>
     * POST /auth/login
     * {
     *   "username": "johnDoe",
     *   "password": "securePassword"
     * }
     * </pre>
     *
     * @param req the login request containing username and password
     * @return a {@link ResponseEntity} containing the authentication response with a JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req){
        return ResponseEntity.ok(authService.login(req.getUsername(), req.getPassword()));
    }
}
