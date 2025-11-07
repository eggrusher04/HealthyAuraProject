package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.*;
import com.FeedEmGreens.HealthyAura.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for handling review-related operations for eateries.
 *
 * <p>This controller provides endpoints to:
 * <ul>
 *     <li>Create, update, and delete user reviews</li>
 *     <li>Fetch reviews and aggregated ratings for eateries</li>
 *     <li>Flag inappropriate reviews for moderation</li>
 * </ul>
 * </p>
 *
 * <p>Endpoints are mapped under <code>/api/eateries/{eateryId}/reviews</code>,
 * and delegate core logic to the {@link ReviewService}.</p>
 *
 * <p>Error responses are handled gracefully with meaningful messages and
 * appropriate HTTP status codes.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/api/eateries/{eateryId}/reviews")
public class ReviewController {

    /**
     * Service responsible for managing review creation, retrieval, updates, and flagging.
     */
    @Autowired
    private ReviewService reviewService;

    /**
     * Creates or updates a user's review for a specific eatery.
     *
     * <p>If the user already has a review for the given eatery, the existing review
     * will be updated instead of creating a new one.</p>
     *
     * <p>Endpoint: <code>POST /api/eateries/{eateryId}/reviews</code></p>
     *
     * @param eateryId the ID of the eatery being reviewed
     * @param request  the review data, including rating, comments, and other fields
     * @return a {@link ResponseEntity} containing success or error messages and the saved review
     */
    // Create or update a review
    // If user has existing review, it will be edited instead
    @PostMapping
    public ResponseEntity<?> createOrUpdateReview(
            @PathVariable Long eateryId,
            @RequestBody ReviewRequest request
    ) {
        try {
            ReviewResponse response = reviewService.createOrUpdateReview(eateryId, request);
            Map<String, Object> result = new HashMap<>();
            result.put("message", response.getUpdatedAt() != null ? "Review updated." : "Review posted.");
            result.put("review", response);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unable to submit review. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves all reviews for a specific eatery.
     *
     * <p>Results can be optionally sorted by a given parameter such as
     * <code>RECENT</code>, <code>RATING_HIGH</code>, or <code>RATING_LOW</code>.</p>
     *
     * <p>Endpoint: <code>GET /api/eateries/{eateryId}/reviews</code></p>
     *
     * @param eateryId the ID of the eatery
     * @param sortBy   optional sorting criteria (defaults to <code>RECENT</code>)
     * @return a {@link ResponseEntity} containing the list of reviews or a message if none exist
     */
    // Get all reviews for an eatery with optional sorting
    @GetMapping
    public ResponseEntity<?> getReviews(
            @PathVariable Long eateryId,
            @RequestParam(required = false, defaultValue = "RECENT") String sortBy
    ) {
        try {
            List<ReviewResponse> reviews = reviewService.getReviewsForEatery(eateryId, sortBy);
            if (reviews.isEmpty()) {
                Map<String, String> message = new HashMap<>();
                message.put("message", "No reviews yet.");
                return ResponseEntity.ok(message);
            }
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Retrieves the currently authenticated user's own review for a specific eatery.
     *
     * <p>If the user has not written a review for this eatery, a message indicating
     * “No review found” will be returned.</p>
     *
     * <p>Endpoint: <code>GET /api/eateries/{eateryId}/reviews/my-review</code></p>
     *
     * @param eateryId the ID of the eatery
     * @return a {@link ResponseEntity} containing the user’s review or an informational message
     */
    // Get user's own review for an eatery (if exists)
    @GetMapping("/my-review")
    public ResponseEntity<?> getMyReview(@PathVariable Long eateryId) {
        try {
            ReviewResponse review = reviewService.getUserReview(eateryId);
            if (review == null) {
                Map<String, String> message = new HashMap<>();
                message.put("message", "No review found.");
                return ResponseEntity.ok(message);
            }
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Updates an existing review for a given eatery.
     *
     * <p>This endpoint allows a user to edit their previous review content or rating.</p>
     *
     * <p>Endpoint: <code>PUT /api/eateries/{eateryId}/reviews/{reviewId}</code></p>
     *
     * @param eateryId the ID of the eatery being reviewed
     * @param reviewId the ID of the review to update
     * @param request  the updated review data
     * @return a {@link ResponseEntity} containing the updated review and a confirmation message
     */
    // Update an existing review
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long eateryId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request
    ) {
        try {
            ReviewResponse response = reviewService.updateReview(reviewId, request);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Review updated.");
            result.put("review", response);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unable to update review. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Deletes a user's review for a specific eatery.
     *
     * <p>This permanently removes the review record and is typically followed by
     * a recalculation of the eatery’s overall ratings.</p>
     *
     * <p>Endpoint: <code>DELETE /api/eateries/{eateryId}/reviews/{reviewId}</code></p>
     *
     * @param eateryId the ID of the eatery associated with the review
     * @param reviewId the ID of the review to delete
     * @return a {@link ResponseEntity} with a success or error message
     */
    // Delete a review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long eateryId,
            @PathVariable Long reviewId
    ) {
        try {
            reviewService.deleteReview(reviewId);
            Map<String, String> result = new HashMap<>();
            result.put("message", "Review deleted.");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unable to delete review. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Flags a review as inappropriate or violating community guidelines.
     *
     * <p>This endpoint allows users to report reviews containing offensive,
     * spammy, or misleading content. The flag is recorded for admin moderation.</p>
     *
     * <p>Endpoint: <code>POST /api/eateries/{eateryId}/reviews/{reviewId}/flag</code></p>
     *
     * @param eateryId the ID of the eatery where the review was posted
     * @param reviewId the ID of the review being flagged
     * @param request  the {@link ReviewFlagRequest} containing the reason for flagging
     * @return a {@link ResponseEntity} with a confirmation or error message
     */
    // Flag a review as inappropriate
    @PostMapping("/{reviewId}/flag")
    public ResponseEntity<?> flagReview(
            @PathVariable Long eateryId,
            @PathVariable Long reviewId,
            @RequestBody ReviewFlagRequest request
    ) {
        try {
            reviewService.flagReview(reviewId, request);
            Map<String, String> result = new HashMap<>();
            result.put("message", "Review reported.");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unable to report review. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves aggregated rating statistics for a specific eatery.
     *
     * <p>The response includes metrics such as average rating, total reviews,
     * and rating distribution across different star levels.</p>
     *
     * <p>Endpoint: <code>GET /api/eateries/{eateryId}/reviews/ratings</code></p>
     *
     * @param eateryId the ID of the eatery
     * @return a {@link ResponseEntity} containing an {@link AggregatedRatingsResponse} object
     */
    // Get aggregated ratings for an eatery
    @GetMapping("/ratings")
    public ResponseEntity<?> getAggregatedRatings(@PathVariable Long eateryId) {
        try {
            AggregatedRatingsResponse ratings = reviewService.getAggregatedRatings(eateryId);
            return ResponseEntity.ok(ratings);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

