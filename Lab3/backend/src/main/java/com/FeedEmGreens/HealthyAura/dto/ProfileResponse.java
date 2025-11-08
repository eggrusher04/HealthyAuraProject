package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing the profile information of a user.
 *
 * <p>This object is used to return user profile details such as username, email,
 * total accumulated points, and saved dietary or food preferences. It is typically
 * used by the {@link com.FeedEmGreens.HealthyAura.controller.ProfileController} in
 * response to profile-related API requests.</p>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "username": "johnDoe",
 *   "email": "john@example.com",
 *   "totalPoints": 480,
 *   "preferences": "Vegetarian, Low Sugar"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.service.ProfileService
 * @see com.FeedEmGreens.HealthyAura.controller.ProfileController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class ProfileResponse {

    /** The username of the user. */
    private String username;

    /** The email address associated with the user account. */
    private String email;

    /** The total reward points accumulated by the user. */
    private int totalPoints;

    /** The user's dietary or personal food preferences (e.g., Vegan, High Protein). */
    private String preferences;

    /**
     * Default no-argument constructor.
     * <p>Creates an empty {@code ProfileResponse} instance.</p>
     */
    public ProfileResponse() {
        // Default constructor
    }

    /**
     * Constructs a {@code ProfileResponse} with all profile details.
     *
     * @param username     the username of the user
     * @param email        the user's email address
     * @param totalPoints  the total accumulated reward points
     * @param preferences  the user's dietary or lifestyle preferences
     */
    public ProfileResponse(String username, String email, int totalPoints, String preferences) {
        this.username = username;
        this.email = email;
        this.totalPoints = totalPoints;
        this.preferences = preferences;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address associated with the user's account.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the total number of points the user has accumulated.
     *
     * @return the total points
     */
    public Integer getTotalPoints() {
        return totalPoints;
    }

    /**
     * Returns the user's stored dietary or food preferences.
     *
     * @return the user’s preferences as a string
     */
    public String getPreferences() {
        return preferences;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username to assign
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address to assign
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the total number of points the user has accumulated.
     *
     * @param totalPoints the total points value
     */
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * Sets the user's preferences.
     *
     * @param preferences the user's dietary or food preferences
     */
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
