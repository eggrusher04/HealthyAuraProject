package com.FeedEmGreens.HealthyAura.dto;

public class ProfileResponse {

    private String username;
    private String email;
    private Integer points;
    private String preferences;

    public ProfileResponse() {
        // Default constructor
    }

    public ProfileResponse(String username, String email, Integer points, String preferences) {
        this.username = username;
        this.email = email;
        this.points = points;
        this.preferences = preferences;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPoints() {
        return points;
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

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
