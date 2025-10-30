package com.FeedEmGreens.HealthyAura.dto;

public class ReviewFlagRequest {
    private String reason; // e.g., "spam", "offensive", "false_information"

    public ReviewFlagRequest() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

