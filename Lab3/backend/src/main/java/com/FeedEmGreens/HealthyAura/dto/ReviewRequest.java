package com.FeedEmGreens.HealthyAura.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a user’s review submission for an eatery.
 *
 * <p>This class is used when a user posts or updates a review, providing numerical
 * ratings for health and hygiene, optional written feedback, and uploaded photos.</p>
 *
 * <p>Typically consumed by the
 * {@link com.FeedEmGreens.HealthyAura.controller.ReviewController} through the
 * <code>/api/eateries/{eateryId}/reviews</code> endpoint.</p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "healthScore": 5,
 *   "hygieneScore": 4,
 *   "textFeedback": "Clean environment and fresh ingredients.",
 *   "photos": [
 *     "https://example.com/image1.jpg",
 *     "https://example.com/image2.jpg"
 *   ]
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Review
 * @see com.FeedEmGreens.HealthyAura.service.ReviewService
 * @see com.FeedEmGreens.HealthyAura.controller.ReviewController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class ReviewRequest {

    /** User-provided health score rating for the eatery (e.g., 1–5 scale). */
    private Integer healthScore;

    /** User-provided hygiene score rating for the eatery (e.g., 1–5 scale). */
    private Integer hygieneScore;

    /** Optional written text feedback describing the user’s experience. */
    private String textFeedback;

    /** Optional list of photo URLs or base64-encoded image strings. */
    private List<String> photos;

    /** Default no-argument constructor. */
    public ReviewRequest() {}

    /**
     * Returns the health score provided by the user.
     *
     * @return the health score (e.g., 1–5)
     */
    public Integer getHealthScore() {
        return healthScore;
    }

    /**
     * Sets the health score rating.
     *
     * @param healthScore the numerical health rating
     */
    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }

    /**
     * Returns the hygiene score provided by the user.
     *
     * @return the hygiene score (e.g., 1–5)
     */
    public Integer getHygieneScore() {
        return hygieneScore;
    }

    /**
     * Sets the hygiene score rating.
     *
     * @param hygieneScore the numerical hygiene rating
     */
    public void setHygieneScore(Integer hygieneScore) {
        this.hygieneScore = hygieneScore;
    }

    /**
     * Returns the text feedback submitted by the user.
     *
     * @return the user’s written review text
     */
    public String getTextFeedback() {
        return textFeedback;
    }

    /**
     * Sets the text feedback describing the user’s experience.
     *
     * @param textFeedback the review comment
     */
    public void setTextFeedback(String textFeedback) {
        this.textFeedback = textFeedback;
    }

    /**
     * Returns a list of photos included in the review.
     *
     * @return list of image URLs or encoded strings
     */
    public List<String> getPhotos() {
        return photos;
    }

    /**
     * Sets the list of photos associated with the review.
     *
     * @param photos list of image URLs or encoded strings
     */
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
