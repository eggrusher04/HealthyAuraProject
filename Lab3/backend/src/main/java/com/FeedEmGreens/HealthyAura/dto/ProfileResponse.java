package com.FeedEmGreens.HealthyAura.dto;

public class ProfileResponse {

    private String username;
    private String email;
    private int totalPoints;
    private String preferences;

    public ProfileResponse() {
        // Default constructor
    }

    public ProfileResponse(String username, String email, int totalPoints, String preferences) {
        this.username = username;
        this.email = email;
        this.totalPoints = totalPoints;
        this.preferences = preferences;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public String getPreferences() {
        return preferences;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
