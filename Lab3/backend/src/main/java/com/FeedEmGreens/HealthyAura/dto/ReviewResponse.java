package com.FeedEmGreens.HealthyAura.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {
    private Long id;
    private Long eateryId;
    private String eateryName;
    private Long userId;
    private String authorAlias; // username
    private Integer healthScore;
    private Integer hygieneScore;
    private String textFeedback;
    private List<String> photos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isOwnReview; // Flag to show "Your review" label

    public ReviewResponse() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEateryId() { return eateryId; }
    public void setEateryId(Long eateryId) { this.eateryId = eateryId; }

    public String getEateryName() { return eateryName; }
    public void setEateryName(String eateryName) { this.eateryName = eateryName; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAuthorAlias() { return authorAlias; }
    public void setAuthorAlias(String authorAlias) { this.authorAlias = authorAlias; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public Integer getHygieneScore() { return hygieneScore; }
    public void setHygieneScore(Integer hygieneScore) { this.hygieneScore = hygieneScore; }

    public String getTextFeedback() { return textFeedback; }
    public void setTextFeedback(String textFeedback) { this.textFeedback = textFeedback; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsOwnReview() { return isOwnReview; }
    public void setIsOwnReview(Boolean isOwnReview) { this.isOwnReview = isOwnReview; }
}

