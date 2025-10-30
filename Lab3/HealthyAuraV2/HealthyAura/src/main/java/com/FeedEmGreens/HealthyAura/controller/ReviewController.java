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

@RestController
@RequestMapping("/api/eateries/{eateryId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

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

