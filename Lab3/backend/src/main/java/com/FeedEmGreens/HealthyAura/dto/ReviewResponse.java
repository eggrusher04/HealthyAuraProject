package com.FeedEmGreens.HealthyAura.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a review submitted by a user for an eatery.
 *
 * <p>This class is used for sending review details from the backend to the frontend,
 * including user scores, written feedback, photos, timestamps, and ownership flags
 * (to indicate if the logged-in user authored the review).</p>
 *
 * <p>It is typically returned by the
 * {@link com.FeedEmGreens.HealthyAura.controller.ReviewController} endpoints when
 * retrieving reviews for an eatery or an individual user's review.</p>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "id": 42,
 *   "eateryId": 10,
 *   "eateryName": "The Salad Stop",
 *   "userId": 5,
 *   "authorAlias": "healthyEater123",
 *   "healthScore": 5,
 *   "hygieneScore": 4,
 *   "textFeedback": "Super fresh ingredients and clean environment!",
 *   "photos": [
 *     "https://example.com/reviews/photo1.jpg"
 *   ],
 *   "createdAt": "2025-11-07T12:15:00",
 *   "updatedAt": "2025-11-07T13:00:00",
 *   "isOwnReview": true
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
public class ReviewResponse {

    /** Unique identifier of the review. */
    private Long id;

    /** The ID of the eatery that this review is associated with. */
    private Long eateryId;

    /** The name of the eatery being reviewed. */
    private String eateryName;

    /** The ID of the user who submitted this review. */
    private Long userId;

    /** The username or display alias of the review author. */
    private String authorAlias;

    /** The health rating given by the user (e.g., 1–5). */
    private Integer healthScore;

    /** The hygiene rating given by the user (e.g., 1–5). */
    private Integer hygieneScore;

    /** Optional text feedback provided by the user. */
    private String textFeedback;

    /** List of photo URLs or encoded images attached to the review. */
    private List<String> photos;

    /** The date and time when the review was created. */
    private LocalDateTime createdAt;

    /** The date and time when the review was last updated. */
    private LocalDateTime updatedAt;

    /** Indicates whether this review belongs to the currently logged-in user. */
    private Boolean isOwnReview;

    /** Default no-argument constructor. */
    public ReviewResponse() {}

    // ------------------ Getters and Setters ------------------

    /** @return the unique ID of the review */
    public Long getId() { return id; }

    /** @param id the unique review ID to set */
    public void setId(Long id) { this.id = id; }

    /** @return the eatery ID associated with this review */
    public Long getEateryId() { return eateryId; }

    /** @param eateryId the eatery ID to associate with this review */
    public void setEateryId(Long eateryId) { this.eateryId = eateryId; }

    /** @return the name of the reviewed eatery */
    public String getEateryName() { return eateryName; }

    /** @param eateryName the name of the eatery being reviewed */
    public void setEateryName(String eateryName) { this.eateryName = eateryName; }

    /** @return the user ID of the review author */
    public Long getUserId() { return userId; }

    /** @param userId the ID of the user who wrote the review */
    public void setUserId(Long userId) { this.userId = userId; }

    /** @return the author’s username or display alias */
    public String getAuthorAlias() { return authorAlias; }

    /** @param authorAlias the author’s display alias or username */
    public void setAuthorAlias(String authorAlias) { this.authorAlias = authorAlias; }

    /** @return the health score assigned by the reviewer */
    public Integer getHealthScore() { return healthScore; }

    /** @param healthScore the health score (1–5 scale) to set */
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    /** @return the hygiene score assigned by the reviewer */
    public Integer getHygieneScore() { return hygieneScore; }

    /** @param hygieneScore the hygiene score (1–5 scale) to set */
    public void setHygieneScore(Integer hygieneScore) { this.hygieneScore = hygieneScore; }

    /** @return the text feedback provided by the user */
    public String getTextFeedback() { return textFeedback; }

    /** @param textFeedback the written feedback to set */
    public void setTextFeedback(String textFeedback) { this.textFeedback = textFeedback; }

    /** @return the list of photo URLs or image strings attached to the review */
    public List<String> getPhotos() { return photos; }

    /** @param photos the list of photo URLs or encoded image strings */
    public void setPhotos(List<String> photos) { this.photos = photos; }

    /** @return the timestamp when the review was created */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt the creation timestamp to set */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @return the timestamp when the review was last updated */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /** @param updatedAt the update timestamp to set */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /** @return {@code true} if this review belongs to the logged-in user, otherwise {@code false} */
    public Boolean getIsOwnReview() { return isOwnReview; }

    /** @param isOwnReview whether this review was authored by the logged-in user */
    public void setIsOwnReview(Boolean isOwnReview) { this.isOwnReview = isOwnReview; }
}
