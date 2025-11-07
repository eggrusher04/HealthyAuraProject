package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing the crowd level display information
 * for a specific eatery.
 *
 * <p>This object is typically used to present real-time or estimated
 * crowd conditions to users in the frontend interface.</p>
 *
 * <p>It contains key display data such as:</p>
 * <ul>
 *     <li>The qualitative crowd level (e.g., "Low", "Moderate", "High")</li>
 *     <li>The estimated queue or waiting time in minutes</li>
 *     <li>A color code (e.g., green, yellow, red) used for quick visual indication</li>
 * </ul>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "crowdLevel": "Moderate",
 *   "estimatedQueueMinutes": 10,
 *   "colorCode": "#FFD700"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.service.CrowdService
 * @see com.FeedEmGreens.HealthyAura.controller.CrowdController
 *
 * @author Rusha
 * @version 1.0
 * @since 2025-11-07
 */
public class CrowdDisplayDTO {

    /** The descriptive crowd level (e.g., "Low", "Moderate", "High"). */
    private String crowdLevel;

    /** The estimated waiting time in minutes. */
    private int estimatedQueueMinutes;

    /** A color code representing the crowd status for UI display. */
    private String colorCode;

    /**
     * Constructs a {@code CrowdDisplayDTO} with the specified details.
     *
     * @param crowdLevel           the crowd level description
     * @param estimatedQueueMinutes the estimated queue time in minutes
     * @param colorCode             the color code for the crowd indicator
     */
    public CrowdDisplayDTO(String crowdLevel, int estimatedQueueMinutes, String colorCode) {
        this.crowdLevel = crowdLevel;
        this.estimatedQueueMinutes = estimatedQueueMinutes;
        this.colorCode = colorCode;
    }

    /**
     * Returns the qualitative description of the crowd level.
     *
     * @return the crowd level (e.g., "Low", "High")
     */
    public String getCrowdLevel() { return crowdLevel; }

    /**
     * Returns the estimated queue or waiting time in minutes.
     *
     * @return the estimated queue duration
     */
    public int getEstimatedQueueMinutes() { return estimatedQueueMinutes; }

    /**
     * Returns the color code representing the current crowd condition.
     *
     * @return a color code string (e.g., "#00FF00" for green)
     */
    public String getColorCode() { return colorCode; }
}
