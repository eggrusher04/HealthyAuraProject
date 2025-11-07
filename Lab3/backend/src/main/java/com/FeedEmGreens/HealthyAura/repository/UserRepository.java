package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD and lookup operations
 * on {@link com.FeedEmGreens.HealthyAura.entity.Users} entities.
 *
 * <p>This repository serves as the data access layer for user-related
 * operations, such as authentication, registration, and profile management.
 * It interacts directly with the {@code users} table to fetch user credentials,
 * verify uniqueness, and support Spring Security authentication flows.</p>
 *
 * <p>Spring Data JPA automatically provides common CRUD methods, while
 * the custom query methods below support username and email-based lookups.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.service.AuthService
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Retrieves a user by their unique username.
     *
     * <p>This method is typically used during authentication and profile retrieval
     * to locate a user by their chosen username.</p>
     *
     * @param username the username of the user
     * @return an {@link Optional} containing the {@link Users} entity if found
     */
    Optional<Users> findByUsername(String username);

    /**
     * Retrieves a user by their registered email address.
     *
     * <p>Used during signup validation, password recovery, or account management
     * workflows to verify that the provided email exists in the system.</p>
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the {@link Users} entity if found
     */
    Optional<Users> findByEmail(String email);

    /**
     * Checks if a user with the given username already exists.
     *
     * <p>This validation method helps prevent duplicate usernames
     * during account registration or admin account creation.</p>
     *
     * @param username the username to check for existence
     * @return {@code true} if the username exists, {@code false} otherwise
     */
    boolean existsByUsername(String username);
}
