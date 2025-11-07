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

/**
 * Service layer responsible for managing user reviews, ratings, and moderation workflows.
 *
 * <p>This class serves as the core business logic for the review system, providing
 * functionality such as:</p>
 * <ul>
 *   <li>Submitting, editing, and deleting reviews</li>
 *   <li>Awarding or deducting points via {@link RewardsService}</li>
 *   <li>Administrative review moderation (approve, hide, delete)</li>
 *   <li>Flagging inappropriate reviews for follow-up</li>
 *   <li>Aggregating review statistics for analytics and recommendations</li>
 * </ul>
 *
 * <p>It interacts closely with repositories for {@link Review}, {@link ReviewFlag},
 * {@link Eatery}, and {@link Users} entities, ensuring transactional safety
 * and data consistency during review submission or moderation.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.repository.ReviewRepository
 * @see com.FeedEmGreens.HealthyAura.repository.ReviewFlagRepository
 * @see com.FeedEmGreens.HealthyAura.repository.EateryRepository
 * @see com.FeedEmGreens.HealthyAura.repository.UserRepository
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 *
 * @version 1.0
 * @since 2025-11-07
 */
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

    @Autowired
    private RewardsService rewardsService;

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    /**
     * Creates a new review or updates an existing one for a specific eatery.
     *
     * <p>Includes validation for:
     * <ul>
     *   <li>Score range (1–5)</li>
     *   <li>Daily review submission limit (max 5/day)</li>
     *   <li>7-day cooldown between reviews for the same eatery</li>
     *   <li>Photo upload limit (max 3)</li>
     * </ul>
     * </p>
     *
     * <p>New reviews earn points, while edited ones do not.</p>
     *
     * @param eateryId the ID of the eatery being reviewed
     * @param request the submitted review details
     * @return a {@link ReviewResponse} DTO representing the created or updated review
     * @throws RuntimeException if the user or eatery cannot be found
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public ReviewResponse createOrUpdateReview(Long eateryId, ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        // Validation
        if (request.getHealthScore() == null || request.getHygieneScore() == null)
            throw new IllegalArgumentException("Health score and Hygiene score are required");

        if (request.getHealthScore() < 1 || request.getHealthScore() > 5 ||
                request.getHygieneScore() < 1 || request.getHygieneScore() > 5)
            throw new IllegalArgumentException("Scores must be between 1 and 5");

        Review existingReview = reviewRepository.findByEateryAndUserAndIsDeletedFalse(eatery, user).orElse(null);
        Review review;

        if (existingReview != null) {
            // Edit existing review
            review = existingReview;
            review.setHealthScore(request.getHealthScore());
            review.setHygieneScore(request.getHygieneScore());
            review.setTextFeedback(request.getTextFeedback());
            if (request.getPhotos() != null) {
                if (request.getPhotos().size() > 3)
                    throw new IllegalArgumentException("Maximum 3 photos allowed");
                review.setPhotos(request.getPhotos());
            }
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            // Validate daily and cooldown limits
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            Long reviewsToday = reviewRepository.countReviewsCreatedTodayByUser(user, startOfDay);
            if (reviewsToday != null && reviewsToday >= 5)
                throw new IllegalArgumentException("Daily review limit reached. You can only submit 5 reviews per day.");

            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            if (!reviewRepository.findRecentSubmissions(eatery, user, sevenDaysAgo).isEmpty())
                throw new IllegalArgumentException("You must wait 7 days before submitting a new review for this eatery");

            // Create new review
            review = new Review(eatery, user, request.getHealthScore(), request.getHygieneScore());
            review.setTextFeedback(request.getTextFeedback());
            if (request.getPhotos() != null) {
                if (request.getPhotos().size() > 3)
                    throw new IllegalArgumentException("Maximum 3 photos allowed");
                review.setPhotos(request.getPhotos());
            }

            // Award points for new reviews
            int pointsAwarded = calculatePointsForReview(request);
            if (pointsAwarded > 0) {
                rewardsService.addPoints(username, pointsAwarded);
                review.setPointsAwarded(pointsAwarded);
            }
        }

        Review saved = reviewRepository.save(review);
        return convertToResponse(saved, true);
    }

    /**
     * Retrieves all visible reviews for an eatery, supporting multiple sorting modes:
     * <ul>
     *   <li><b>RECENT</b> – newest first (default)</li>
     *   <li><b>HEALTH</b> – highest health rating first</li>
     *   <li><b>HYGIENE</b> – highest hygiene rating first</li>
     * </ul>
     *
     * @param eateryId the target eatery’s ID
     * @param sortBy sorting option
     * @return a list of {@link ReviewResponse} DTOs
     */
    public List<ReviewResponse> getReviewsForEatery(Long eateryId, String sortBy) {
        String username = SecurityContextHolder.getContext().getAuthentication() != null ?
                SecurityContextHolder.getContext().getAuthentication().getName() : null;
        Users currentUser = username != null ? userRepository.findByUsername(username).orElse(null) : null;

        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        List<Review> reviews;
        switch (sortBy != null ? sortBy.toUpperCase() : "RECENT") {
            case "HEALTH" -> reviews = reviewRepository.findByEateryOrderByHealthScoreDesc(eatery);
            case "HYGIENE" -> reviews = reviewRepository.findByEateryOrderByHygieneScoreDesc(eatery);
            default -> reviews = reviewRepository.findVisibleByEateryOrderByCreatedAtDesc(eatery);
        }

        return reviews.stream()
                .map(r -> convertToResponse(r, currentUser != null && r.getUser().getId().equals(currentUser.getId())))
                .collect(Collectors.toList());
    }

    // ===== ADMIN MODERATION OPERATIONS =====

    /**
     * Approves a flagged review (clears flag as dismissed).
     *
     * @param reviewId the ID of the reviewed item
     * @param flagId the associated flag to dismiss
     * @param notes optional admin notes
     */
    @Transactional
    public void approveReviewByAdmin(Long reviewId, Long flagId, String notes) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (flagId != null) {
            ReviewFlag flag = reviewFlagRepository.findById(flagId)
                    .orElseThrow(() -> new IllegalArgumentException("Flag not found: " + flagId));
            if (!"PENDING".equals(flag.getStatus()))
                throw new IllegalArgumentException("Flag has already been resolved");

            flag.setStatus("DISMISSED");
            flag.setAdminNotes(notes != null ? notes : ("Approved by: " + admin));
            flag.setReviewedAt(LocalDateTime.now());
            reviewFlagRepository.save(flag);
        }

        logAdminAction(admin, "REVIEW_APPROVE", "REVIEW", review.getId(), review.getEatery().getId(),
                notes != null ? notes : "Approved review and dismissed flag");
    }

    /** Hides a review (keeps in DB but invisible to public). */
    @Transactional
    public void hideReviewByAdmin(Long reviewId, String reason) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        if (reason == null || reason.trim().isEmpty())
            throw new IllegalArgumentException("Reason is required to hide a review");

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (Boolean.TRUE.equals(review.getIsDeleted()))
            throw new IllegalArgumentException("Cannot hide a deleted review");

        review.setIsHidden(true);
        review.setHiddenAt(LocalDateTime.now());
        review.setHiddenReason(reason);
        review.setModeratedByAdminUsername(admin);
        reviewRepository.save(review);

        deductPointsForReview(review);

        // Resolve pending flags
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

    /** Soft-deletes a review (user data remains for analytics but is hidden). */
    @Transactional
    public void deleteReviewByAdmin(Long reviewId, String reason) {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        if (reason == null || reason.trim().isEmpty())
            throw new IllegalArgumentException("Reason is required to delete a review");

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (Boolean.TRUE.equals(review.getIsDeleted()))
            throw new IllegalArgumentException("Review has already been deleted");

        review.setIsDeleted(true);
        review.setModeratedByAdminUsername(admin);
        reviewRepository.save(review);

        deductPointsForReview(review);

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

    // ===== USER REVIEW OPERATIONS =====

    /** Updates an existing review belonging to the current user. */
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (review.getIsDeleted())
            throw new IllegalArgumentException("Review has been deleted");

        if (!review.getUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("You can only edit your own reviews");

        if (request.getPhotos() != null && request.getPhotos().size() > 3)
            throw new IllegalArgumentException("Maximum 3 photos allowed");

        if (request.getHealthScore() != null)
            review.setHealthScore(request.getHealthScore());
        if (request.getHygieneScore() != null)
            review.setHygieneScore(request.getHygieneScore());
        if (request.getTextFeedback() != null)
            review.setTextFeedback(request.getTextFeedback());
        if (request.getPhotos() != null)
            review.setPhotos(request.getPhotos());

        review.setUpdatedAt(LocalDateTime.now());
        return convertToResponse(reviewRepository.save(review), true);
    }

    /** Soft-deletes a user’s own review. */
    @Transactional
    public void deleteReview(Long reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (review.getIsDeleted())
            throw new IllegalArgumentException("Review has already been deleted");

        if (!review.getUser().getId().equals(user.getId()))
            throw new IllegalArgumentException("You can only delete your own reviews");

        if (Boolean.TRUE.equals(review.getIsHidden()))
            throw new IllegalArgumentException("Cannot delete a hidden review. Please contact support.");

        review.setIsDeleted(true);
        reviewRepository.save(review);
        deductPointsForReview(review);
    }

    /** Allows a user to flag an inappropriate review for admin moderation. */
    @Transactional
    public void flagReview(Long reviewId, ReviewFlagRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (review.getIsDeleted())
            throw new IllegalArgumentException("Cannot flag a deleted review");

        if (reviewFlagRepository.existsByReviewIdAndUserId(reviewId, user.getId()))
            throw new IllegalArgumentException("You have already flagged this review");

        if (request.getReason() == null || request.getReason().trim().isEmpty())
            throw new IllegalArgumentException("Flag reason is required");

        reviewFlagRepository.save(new ReviewFlag(review, user, request.getReason()));
    }

    /** Retrieves aggregated review metrics for display on eatery profiles. */
    public AggregatedRatingsResponse getAggregatedRatings(Long eateryId) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        Double avgHealth = reviewRepository.getAverageHealthScore(eatery);
        Double avgHygiene = reviewRepository.getAverageHygieneScore(eatery);
        Long count = reviewRepository.getReviewCount(eatery);

        return new AggregatedRatingsResponse(avgHealth, avgHygiene, count);
    }

    /** Fetches the logged-in user’s own review for a specific eatery, if present. */
    public ReviewResponse getUserReview(Long eateryId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        Review review = reviewRepository.findByEateryAndUserAndIsDeletedFalse(eatery, user).orElse(null);
        return review == null ? null : convertToResponse(review, true);
    }

    /** Returns a list of all pending review flags for admin moderation. */
    public List<ReviewFlag> getPendingFlags() {
        return reviewFlagRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    // ===== HELPER METHODS =====

    /** Calculates total points earned from a review submission. */
    private int calculatePointsForReview(ReviewRequest request) {
        int points = 10;
        if (request.getTextFeedback() != null && request.getTextFeedback().trim().length() >= 10)
            points += 5;
        if (request.getPhotos() != null && !request.getPhotos().isEmpty())
            points += Math.min(request.getPhotos().size(), 3) * 5;
        return points;
    }

    /** Deducts points when a review is deleted or hidden. */
    private void deductPointsForReview(Review review) {
        if (review.getPointsAwarded() != null && review.getPointsAwarded() > 0)
            rewardsService.deductPoints(review.getUser().getUsername(), review.getPointsAwarded());
    }

    /** Converts a {@link Review} entity into a simplified {@link ReviewResponse} DTO. */
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

    /** Logs an administrative action into the audit trail. */
    private void logAdminAction(String adminUsername, String actionType, String targetType, Long targetId, Long eateryId, String details) {
        AdminActionLog log = new AdminActionLog(adminUsername, actionType, targetType, targetId, eateryId, details, LocalDateTime.now());
        adminActionLogRepository.save(log);
    }
}
