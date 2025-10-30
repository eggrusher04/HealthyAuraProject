package com.FeedEmGreens.HealthyAura.dto;

import java.util.List;

public class ReviewRequest {
    private Integer healthScore;
    private Integer hygieneScore;
    private String textFeedback;
    private List<String> photos; // URLs or base64 encoded strings

    public ReviewRequest() {}

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public Integer getHygieneScore() { return hygieneScore; }
    public void setHygieneScore(Integer hygieneScore) { this.hygieneScore = hygieneScore; }

    public String getTextFeedback() { return textFeedback; }
    public void setTextFeedback(String textFeedback) { this.textFeedback = textFeedback; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
}

