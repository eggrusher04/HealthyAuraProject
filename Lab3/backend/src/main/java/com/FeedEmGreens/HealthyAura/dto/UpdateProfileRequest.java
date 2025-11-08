package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) used for updating a user's profile preferences.
 *
 * <p>This class is typically used when a logged-in user updates their dietary,
 * lifestyle, or general food preferences through the profile settings page.
 * The preferences are stored as a descriptive string in the database.</p>
 *
 * <p>Handled by the
 * {@link com.FeedEmGreens.HealthyAura.controller.ProfileController}
 * through the <code>/profile/me</code> endpoint.</p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "preferences": "Vegan, High Protein, Low Sugar"
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
public class UpdateProfileRequest {

    /** The updated user preferences (e.g., "Vegan, Low Fat, High Protein"). */
    private String preferences;

    /** Default no-argument constructor. */
    public UpdateProfileRequest() {
        // Default constructor
    }

    /**
     * Constructs an {@code UpdateProfileRequest} with the given preferences.
     *
     * @param preferences the new preferences string to update
     */
    public UpdateProfileRequest(String preferences) {
        this.preferences = preferences;
    }

    /**
     * Returns the user's updated preferences.
     *
     * @return the preferences string
     */
    public String getPreferences() {
        return preferences;
    }

    /**
     * Sets the user's updated preferences.
     *
     * @param preferences the preferences string to set
     */
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
