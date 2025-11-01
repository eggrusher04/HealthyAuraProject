package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.*;
import com.FeedEmGreens.HealthyAura.entity.*;
import com.FeedEmGreens.HealthyAura.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewFlagRepository reviewFlagRepository;

    @Autowired
    private EateryRepository eateryRepository;

    @Autowired
    private UserRepository userRepository;

    // Create or update a review for an eatery
    // If user has an existing review, it opens it for editing instead of creating a new one
    @Transactional
    public ReviewResponse createOrUpdateReview(Long eateryId, ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        // Validate required fields
        if (request.getHealthScore() == null || request.getHygieneScore() == null) {
            throw new IllegalArgumentException("Health score and Hygiene score are required");
        }
        if (request.getHealthScore() < 1 || request.getHealthScore() > 5 ||
            request.getHygieneScore() < 1 || request.getHygieneScore() > 5) {
            throw new IllegalArgumentException("Scores must be between 1 and 5");
        }

        // Check if user already has a review for this eatery
        Review existingReview = reviewRepository.findByEateryAndUserAndIsDeletedFalse(eatery, user)
                .orElse(null);

        Review review;

        if (existingReview != null) {
            // Edit existing review
            review = existingReview;
            review.setHealthScore(request.getHealthScore());
            review.setHygieneScore(request.getHygieneScore());
            review.setTextFeedback(request.getTextFeedback());
            if (request.getPhotos() != null) {
                if (request.getPhotos().size() > 3) {
                    throw new IllegalArgumentException("Maximum 3 photos allowed");
                }
                review.setPhotos(request.getPhotos());
            }
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            // check 7 days cooldown for new reviews
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            List<Review> recentSubmissions = reviewRepository.findRecentSubmissions(eatery, user, sevenDaysAgo);
            if (!recentSubmissions.isEmpty()) {
                throw new IllegalArgumentException("You must wait 7 days before submitting a new review for this eatery");
            }

            // create new review
            review = new Review(eatery, user, request.getHealthScore(), request.getHygieneScore());
            review.setTextFeedback(request.getTextFeedback());
            if (request.getPhotos() != null) {
                if (request.getPhotos().size() > 3) {
                    throw new IllegalArgumentException("Maximum 3 photos allowed");
                }
                review.setPhotos(request.getPhotos());
            }
        }

        Review saved = reviewRepository.save(review);
        return convertToResponse(saved, true);
    }

    //Get all reviews for an eatery with sorting options
    public List<ReviewResponse> getReviewsForEatery(Long eateryId, String sortBy) {
        String username = SecurityContextHolder.getContext().getAuthentication() != null ?
                SecurityContextHolder.getContext().getAuthentication().getName() : null;
        Users currentUser = username != null ? 
                userRepository.findByUsername(username).orElse(null) : null;

        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        List<Review> reviews;
        switch (sortBy != null ? sortBy.toUpperCase() : "RECENT") {
            case "HEALTH":
                reviews = reviewRepository.findByEateryOrderByHealthScoreDesc(eatery);
                break;
            case "HYGIENE":
                reviews = reviewRepository.findByEateryOrderByHygieneScoreDesc(eatery);
                break;
            default:
                reviews = reviewRepository.findVisibleByEateryOrderByCreatedAtDesc(eatery);
                break;
        }

        return reviews.stream()
                .map(review -> convertToResponse(review, 
                    currentUser != null && review.getUser().getId().equals(currentUser.getId())))
                .collect(Collectors.toList());
    }

    // Admin: approve a review (clears related flags, keeps it visible)
    @Transactional
    public void approveReviewByAdmin(Long reviewId, Long flagId, String notes) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (flagId != null) {
            ReviewFlag flag = reviewFlagRepository.findById(flagId)
                    .orElseThrow(() -> new IllegalArgumentException("Flag not found: " + flagId));
            if (!"PENDING".equals(flag.getStatus())) {
                throw new IllegalArgumentException("Flag has already been resolved");
            }
            flag.setStatus("DISMISSED");
            flag.setAdminNotes(notes != null ? notes : ("Approved by: " + admin));
            flag.setReviewedAt(LocalDateTime.now());
            reviewFlagRepository.save(flag);
        }

        logAdminAction(admin, "REVIEW_APPROVE", "REVIEW", review.getId(), review.getEatery().getId(),
                notes != null ? notes : "Approved review and dismissed flag");
    }

    // Admin: hide a review (requires reason)
    @Transactional
    public void hideReviewByAdmin(Long reviewId, String reason) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required to hide a review");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        if (Boolean.TRUE.equals(review.getIsDeleted())) {
            throw new IllegalArgumentException("Cannot hide a deleted review");
        }

        review.setIsHidden(true);
        review.setHiddenAt(LocalDateTime.now());
        review.setHiddenReason(reason);
        review.setModeratedByAdminUsername(admin);
        reviewRepository.save(review);

        // resolve all pending flags for this review as RESOLVED
        for (ReviewFlag rf : reviewFlagRepository.findByReviewId(reviewId)) {
            if ("PENDING".equals(rf.getStatus())) {
                rf.setStatus("RESOLVED");
                rf.setAdminNotes("Hidden by: " + admin + "; Reason: " + reason);
                rf.setReviewedAt(LocalDateTime.now());
                reviewFlagRepository.save(rf);
            }
        }

        logAdminAction(admin, "REVIEW_HIDE", "REVIEW", review.getId(), review.getEatery().getId(),
                "Hidden. Reason: " + reason);
    }

    // Admin: delete a review (soft-delete) requires reason
    @Transactional
    public void deleteReviewByAdmin(Long reviewId, String reason) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required to delete a review");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (Boolean.TRUE.equals(review.getIsDeleted())) {
            throw new IllegalArgumentException("Review has already been deleted");
        }

        review.setIsDeleted(true);
        review.setModeratedByAdminUsername(admin);
        reviewRepository.save(review);

        // resolve all pending flags as RESOLVED
        for (ReviewFlag rf : reviewFlagRepository.findByReviewId(reviewId)) {
            if ("PENDING".equals(rf.getStatus())) {
                rf.setStatus("RESOLVED");
                rf.setAdminNotes("Deleted by: " + admin + "; Reason: " + reason);
                rf.setReviewedAt(LocalDateTime.now());
                reviewFlagRepository.save(rf);
            }
        }

        logAdminAction(admin, "REVIEW_DELETE", "REVIEW", review.getId(), review.getEatery().getId(),
                "Deleted. Reason: " + reason);
    }

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    private void logAdminAction(String adminUsername, String actionType, String targetType, Long targetId, Long eateryId, String details) {
        AdminActionLog log = new AdminActionLog(adminUsername, actionType, targetType, targetId, eateryId, details, LocalDateTime.now());
        adminActionLogRepository.save(log);
    }

    // Update an existing review (alternative endpoint for explicit updates)
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (review.getIsDeleted()) {
            throw new IllegalArgumentException("Review has been deleted");
        }

        // Check ownership
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only edit your own reviews");
        }

        // Validate and update
        if (request.getHealthScore() != null) {
            if (request.getHealthScore() < 1 || request.getHealthScore() > 5) {
                throw new IllegalArgumentException("Health score must be between 1 and 5");
            }
            review.setHealthScore(request.getHealthScore());
        }

        if (request.getHygieneScore() != null) {
            if (request.getHygieneScore() < 1 || request.getHygieneScore() > 5) {
                throw new IllegalArgumentException("Hygiene score must be between 1 and 5");
            }
            review.setHygieneScore(request.getHygieneScore());
        }

        if (request.getTextFeedback() != null) {
            review.setTextFeedback(request.getTextFeedback());
        }

        if (request.getPhotos() != null) {
            if (request.getPhotos().size() > 3) {
                throw new IllegalArgumentException("Maximum 3 photos allowed");
            }
            review.setPhotos(request.getPhotos());
        }

        review.setUpdatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        return convertToResponse(saved, true);
    }

    // Delete a review (soft delete which means it is not deleted from the database, but is marked as deleted)
    // Can only be done by the user who created the review
    @Transactional
    public void deleteReview(Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (review.getIsDeleted()) {
            throw new IllegalArgumentException("Review has already been deleted");
        }

        // Check ownership
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }

        review.setIsDeleted(true);
        reviewRepository.save(review);
    }

    // Authenticated users can flag a review as inappropriate
    @Transactional
    public void flagReview(Long reviewId, ReviewFlagRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (review.getIsDeleted()) {
            throw new IllegalArgumentException("Cannot flag a deleted review");
        }

        // Check if user already flagged this review
        if (reviewFlagRepository.existsByReviewIdAndUserId(reviewId, user.getId())) {
            throw new IllegalArgumentException("You have already flagged this review");
        }

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Flag reason is required");
        }

        ReviewFlag flag = new ReviewFlag(review, user, request.getReason());
        reviewFlagRepository.save(flag);
    }

    // Get aggregated ratings for an eatery
    public AggregatedRatingsResponse getAggregatedRatings(Long eateryId) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        Double avgHealth = reviewRepository.getAverageHealthScore(eatery);
        Double avgHygiene = reviewRepository.getAverageHygieneScore(eatery);
        Long count = reviewRepository.getReviewCount(eatery);

        return new AggregatedRatingsResponse(avgHealth, avgHygiene, count);
    }

    // Get user's review for a specific eatery (if exists)
    public ReviewResponse getUserReview(Long eateryId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        Review review = reviewRepository.findByEateryAndUserAndIsDeletedFalse(eatery, user)
                .orElse(null);

        if (review == null) {
            return null;
        }

        return convertToResponse(review, true);
    }

    // Get flagged reviews for admin moderation queue
    public List<ReviewFlag> getPendingFlags() {
        return reviewFlagRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    // Helper method to convert Review entity to ReviewResponse DTO
    private ReviewResponse convertToResponse(Review review, boolean isOwnReview) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setEateryId(review.getEatery().getId());
        response.setEateryName(review.getEatery().getName());
        response.setUserId(review.getUser().getId());
        response.setAuthorAlias(review.getUser().getUsername());
        response.setHealthScore(review.getHealthScore());
        response.setHygieneScore(review.getHygieneScore());
        response.setTextFeedback(review.getTextFeedback());
        response.setPhotos(review.getPhotos());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        response.setIsOwnReview(isOwnReview);
        return response;
    }
}

