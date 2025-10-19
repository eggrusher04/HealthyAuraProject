package com.FeedEmGreens.HealthyAura.dto;

public class RewardResponse {

    private String username;
    private String rewardName;
    private String description;
    private int pointsUsed;
    private int pointsLeft;
    private String message;

    public RewardResponse(String username, String rewardName, String description,
                          int pointsUsed, int pointsLeft, String message) {
        this.username = username;
        this.rewardName = rewardName;
        this.description = description;
        this.pointsUsed = pointsUsed;
        this.pointsLeft = pointsLeft;
        this.message = message;
    }

    // Getters
    public String getUsername() { return username; }
    public String getRewardName() { return rewardName; }
    public String getDescription() { return description; }
    public int getPointsUsed() { return pointsUsed; }
    public int getPointsLeft() { return pointsLeft; }
    public String getMessage() { return message; }
}
