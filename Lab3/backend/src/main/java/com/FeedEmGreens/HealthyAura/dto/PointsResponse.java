package com.FeedEmGreens.HealthyAura.dto;

public class PointsResponse {

    private String username;
    private int totalPoints;
    private int redeemedPoints;
    private String lastUpdated;

    // Constructor
    public PointsResponse(String username, int totalPoints, int redeemedPoints, String lastUpdated) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.redeemedPoints = redeemedPoints;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getRedeemedPoints() {
        return redeemedPoints;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
