package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) used when a user flags a review as inappropriate or misleading.
 *
 * <p>This object captures the reason a review is being reported and is typically
 * sent as part of a POST request body to the
 * {@link com.FeedEmGreens.HealthyAura.controller.ReviewController} endpoint:
 * <code>/api/eateries/{eateryId}/reviews/{reviewId}/flag</code>.</p>
 *
 * <p>Common reasons may include:
 * <ul>
 *   <li><b>spam</b> — Unrelated or promotional content</li>
 *   <li><b>offensive</b> — Contains hate speech or harassment</li>
 *   <li><b>false_information</b> — Misleading or inaccurate statements</li>
 * </ul>
 * </p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "reason": "offensive"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.ReviewFlag
 * @see com.FeedEmGreens.HealthyAura.service.ReviewService
 * @see com.FeedEmGreens.HealthyAura.controller.ReviewController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class ReviewFlagRequest {

    /**
     * The reason the user is flagging the review
     * (e.g., "spam", "offensive", or "false_information").
     */
    private String reason;

    /** Default no-argument constructor. */
    public ReviewFlagRequest() {}

    /**
     * Returns the reason provided for flagging the review.
     *
     * @return the reason string (e.g., "spam", "offensive")
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for flagging the review.
     *
     * @param reason the reason string to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
