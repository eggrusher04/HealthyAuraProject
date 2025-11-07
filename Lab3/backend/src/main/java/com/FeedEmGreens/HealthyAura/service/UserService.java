package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer providing operations related to {@link Users} management.
 *
 * <p>This class serves as an intermediary between the controller layer and
 * the {@link UserRepository}, handling retrieval and potential updates of
 * user data. It can be easily extended to support registration, profile
 * updates, or user deactivation logic in the future.</p>
 *
 * <p>Responsibilities include:
 * <ul>
 *   <li>Fetching user entities by username</li>
 *   <li>Providing a clean abstraction over repository-level access</li>
 *   <li>Serving as a base for higher-level services such as authentication
 *       and profile management</li>
 * </ul>
 * </p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.repository.UserRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Finds a {@link Users} entity by its username.
     *
     * <p>Typically used by authentication, profile, or rewards services
     * to validate the existence of a user or fetch their information.</p>
     *
     * @param username the unique username of the user
     * @return an {@link Optional} containing the user if found, or empty if not found
     */
    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // -------------------------------------------------------------------------
    // Future Extensions
    // -------------------------------------------------------------------------
    // Example:
    // public Users registerNewUser(SignupRequest request) { ... }
    // public Users updateProfile(String username, UserUpdateRequest request) { ... }
    // public void deactivateUser(String username) { ... }
}
