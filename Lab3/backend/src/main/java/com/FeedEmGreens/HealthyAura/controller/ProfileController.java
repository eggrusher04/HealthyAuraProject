package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.ProfileResponse;
import com.FeedEmGreens.HealthyAura.dto.UpdateProfileRequest;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for managing user profile operations such as
 * retrieving profile details, updating preferences, and modifying account information.
 *
 * <p>This controller exposes authenticated endpoints under <code>/profile</code> for:
 * <ul>
 *     <li>Fetching the current user's profile details</li>
 *     <li>Updating user preferences</li>
 *     <li>Retrieving points for rewards tracking</li>
 *     <li>Updating account information (email or password)</li>
 * </ul>
 * </p>
 *
 * <p>All actions are restricted to the authenticated user, retrieved from
 * the current security context via {@link SecurityContextHolder}.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/profile")
public class ProfileController {

    /**
     * Service layer that handles profile retrieval and updates.
     */
    private final ProfileService profileService;

    /**
     * Constructs a {@code ProfileController} with the specified {@link ProfileService}.
     *
     * @param profileService the service used to perform profile-related operations
     */
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Safely retrieves the total points of a given user.
     *
     * <p>If the user has no associated {@code Points} record, this method returns 0
     * to prevent null pointer exceptions.</p>
     *
     * @param user the {@link Users} object whose points are being retrieved
     * @return the total number of points the user has, or 0 if unavailable
     */
    private int getUserTotalPoints(Users user) {
        return (user.getPoints() != null) ? user.getPoints().getTotalPoints() : 0;
    }

    /**
     * Retrieves the authenticated user's profile details.
     *
     * <p>The profile includes the username, email, preferences, and total reward points.</p>
     * <p>Endpoint: <code>GET /profile/me</code></p>
     *
     * @return a {@link ResponseEntity} containing a {@link ProfileResponse} with user information
     */
    // Get profile by username (for now, via query param)
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = profileService.getUserProfile(username);

        // Safely extract total points
        int totalPoints = getUserTotalPoints(user);

        ProfileResponse response = new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                totalPoints,
                user.getPreferences()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Updates the authenticated user's preferences (e.g., dietary tags or food preferences).
     *
     * <p>Endpoint: <code>PUT /profile/me</code></p>
     *
     * @param req the {@link UpdateProfileRequest} containing the new preferences list
     * @return a {@link ResponseEntity} containing the updated {@link ProfileResponse}
     */
    // Update preferences for a given username
    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest req
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        profileService.updatePreferences(username, req.getPreferences());

        Users updatedUser = profileService.getUserProfile(username);

        int totalPoints = getUserTotalPoints(updatedUser);


        ProfileResponse response = new ProfileResponse(
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                totalPoints,
                updatedUser.getPreferences()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves only the user's total reward points.
     *
     * <p>This endpoint supports integration with the rewards module
     * to display or redeem user points.</p>
     * <p>Endpoint: <code>GET /profile/me/points</code></p>
     *
     * @return a {@link ResponseEntity} containing a map with the key <code>"points"</code> and its integer value
     */
    // Get only user points (for rewards module)
    @GetMapping("/me/points")
    public ResponseEntity<Map<String, Integer>> getPoints() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = profileService.getUserProfile(username);

        int totalPoints = getUserTotalPoints(user);

        Map<String, Integer> pointsMap = new HashMap<>();
        pointsMap.put("points", totalPoints);

        return ResponseEntity.ok(pointsMap);
    }

    /**
     * Updates the authenticated user's email address.
     *
     * <p>The request body should include a new <code>email</code> field.
     * The user's email is updated in the database and returned in the response.</p>
     *
     * <p>Endpoint: <code>PUT /profile/me/email</code></p>
     *
     * @param req a map containing the new email address (key: "email")
     * @return a {@link ResponseEntity} containing a confirmation message and updated email
     */
    @PutMapping("/me/email")
    public ResponseEntity<Map<String, String>> updateEmail(@RequestBody Map<String, String> req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newEmail = req.get("email");

        Users updatedUser = profileService.updateEmail(username, newEmail);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email updated successfully");
        response.put("email", updatedUser.getEmail());

        return ResponseEntity.ok(response);
    }

    /**
     * Updates the authenticated user's password.
     *
     * <p>The request body should include a new <code>password</code> field.
     * The password is securely updated and encrypted via the {@link ProfileService}.</p>
     *
     * <p>Endpoint: <code>PUT /profile/me/password</code></p>
     *
     * @param req a map containing the new password (key: "password")
     * @return a {@link ResponseEntity} containing a success message
     */
    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody Map<String, String> req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newPassword = req.get("password");

        profileService.updatePassword(username, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return ResponseEntity.ok(response);
    }

}