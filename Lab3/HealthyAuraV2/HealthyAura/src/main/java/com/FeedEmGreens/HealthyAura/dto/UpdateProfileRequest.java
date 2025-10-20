package com.FeedEmGreens.HealthyAura.dto;

public class UpdateProfileRequest {

    private String preferences;

    public UpdateProfileRequest() {
        // Default constructor
    }

    public UpdateProfileRequest(String preferences) {
        this.preferences = preferences;
    }

    // Getter
    public String getPreferences() {
        return preferences;
    }

    // Setter
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
