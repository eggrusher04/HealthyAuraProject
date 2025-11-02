package com.FeedEmGreens.HealthyAura.dto;

public class CrowdDisplayDTO {
    private String crowdLevel;
    private int estimatedQueueMinutes;
    private String colorCode;

    public CrowdDisplayDTO(String crowdLevel, int estimatedQueueMinutes, String colorCode) {
        this.crowdLevel = crowdLevel;
        this.estimatedQueueMinutes = estimatedQueueMinutes;
        this.colorCode = colorCode;
    }

    public String getCrowdLevel() { return crowdLevel; }
    public int getEstimatedQueueMinutes() { return estimatedQueueMinutes; }
    public String getColorCode() { return colorCode; }
}
