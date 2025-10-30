package com.FeedEmGreens.HealthyAura.dto;

public class AggregatedRatingsResponse {
    private Double averageHealthScore; // Rounded to 1 decimal place
    private Double averageHygieneScore; // Rounded to 1 decimal place
    private Long totalReviews;

    public AggregatedRatingsResponse() {}

    public AggregatedRatingsResponse(Double averageHealthScore, Double averageHygieneScore, Long totalReviews) {
        this.averageHealthScore = averageHealthScore != null ? 
            Math.round(averageHealthScore * 10.0) / 10.0 : null;
        this.averageHygieneScore = averageHygieneScore != null ? 
            Math.round(averageHygieneScore * 10.0) / 10.0 : null;
        this.totalReviews = totalReviews;
    }

    // Getters and setters
    public Double getAverageHealthScore() { return averageHealthScore; }
    public void setAverageHealthScore(Double averageHealthScore) { 
        this.averageHealthScore = averageHealthScore != null ? 
            Math.round(averageHealthScore * 10.0) / 10.0 : null;
    }

    public Double getAverageHygieneScore() { return averageHygieneScore; }
    public void setAverageHygieneScore(Double averageHygieneScore) { 
        this.averageHygieneScore = averageHygieneScore != null ? 
            Math.round(averageHygieneScore * 10.0) / 10.0 : null;
    }

    public Long getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
}

