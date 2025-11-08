package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Service layer responsible for handling user profile management operations
 * such as viewing and updating user information, preferences, and account security.
 *
 * <p>This class interacts with the {@link UserRepository} to perform CRUD operations
 * on user-related data and also manages initialization of {@link Points} entities
 * when needed.</p>
 *
 * <p>All business logic related to user personalization (e.g. dietary preferences)
 * and account management (e.g. password updates) is encapsulated here, ensuring
 * a clean separation from controller logic and repository access.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.repository.UserRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class ProfileService {

    /** Repository for user entity access and persistence. */
    @Autowired
    private UserRepository userRepository;

    /** Password encoder used for secure hashing during password updates. */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Retrieves the full profile details of a specific user.
     *
     * <p>This includes all persisted fields from the {@link Users} entity,
     * such as email, preferences, and associated points data.</p>
     *
     * @param username the username of the profile to fetch
     * @return a {@link Users} object containing the user’s profile information
     * @throws RuntimeException if no user exists with the given username
     */
    public Users getUserProfile(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }
        return userOpt.get();
    }

    /**
     * Updates the preferences field of the user’s profile.
     *
     * <p>This method allows users to save their personalized dietary preferences
     * or other textual configurations that may influence the recommendation engine.</p>
     *
     * @param username the username of the user whose preferences are to be updated
     * @param preferences the new preference string to be stored
     * @return the updated {@link Users} entity after persistence
     * @throws RuntimeException if the specified user does not exist
     */
    public Users updatePreferences(String username, String preferences) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        Users user = userOpt.get();
        user.setPreferences(preferences);
        return userRepository.save(user);
    }

    /**
     * Ensures that a {@link Points} record exists for the given user.
     *
     * <p>If no points entity is currently associated with the user,
     * this method creates a new {@link Points} instance initialized with
     * a total and redeemed balance of 0.</p>
     *
     * <p>This is particularly useful for backward compatibility or
     * data migration where older users may not yet have points entries.</p>
     *
     * @param username the username of the user whose points record should be initialized
     * @throws RuntimeException if the specified user cannot be found
     */
    public void initializeUserPoints(String username) {
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        Users user = userOpt.get();
        if (user.getPoints() == null) {
            Points points = new Points(user);
            user.setPoints(points);
            userRepository.save(user);
        }
    }

    /**
     * Updates the email address of a user account.
     *
     * <p>This method can be used during profile editing to replace
     * the existing email address with a new one. It performs a direct update
     * without additional verification checks.</p>
     *
     * @param username the username of the user whose email is to be updated
     * @param newEmail the new email address to assign
     * @return the updated {@link Users} entity
     * @throws RuntimeException if the user does not exist
     */
    public Users updateEmail(String username, String newEmail) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(newEmail);
        return userRepository.save(user);
    }

    /**
     * Updates the password of a user account.
     *
     * <p>The new password is hashed securely using BCrypt before being stored,
     * ensuring compliance with secure password handling best practices.</p>
     *
     * @param username the username of the user whose password will be updated
     * @param rawPassword the plain-text new password to hash and save
     * @throws RuntimeException if the user does not exist
     */
    public void updatePassword(String username, String rawPassword) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(encoder.encode(rawPassword));
        userRepository.save(user);
    }
}
