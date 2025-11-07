package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.ReviewFlag;
import com.FeedEmGreens.HealthyAura.entity.Review;
import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD and custom query operations
 * on {@link com.FeedEmGreens.HealthyAura.entity.ReviewFlag} entities.
 *
 * <p>This repository manages review flag records created by users to report
 * inappropriate or misleading reviews. It supports admin moderation workflows,
 * flag tracking, and duplicate prevention for user-submitted flags.</p>
 *
 * <p>Spring Data JPA automatically implements the standard CRUD operations,
 * while the defined query methods below provide domain-specific filtering
 * and retrieval capabilities.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.ReviewFlag
 * @see com.FeedEmGreens.HealthyAura.entity.Review
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.service.ReviewService
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface ReviewFlagRepository extends JpaRepository<ReviewFlag, Long> {

    /**
     * Retrieves all review flags that match the specified status,
     * ordered by their creation date in descending order.
     *
     * <p>Commonly used for the admin moderation dashboard to show
     * “PENDING” flags awaiting review.</p>
     *
     * @param status the moderation status (e.g., "PENDING", "REVIEWED")
     * @return a list of {@link ReviewFlag} entities sorted by most recent first
     */
    List<ReviewFlag> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Retrieves all review flags that match both a specific status
     * and reason, ordered by creation date in descending order.
     *
     * <p>Useful for targeted filtering when moderators want to
     * address reports of a certain type (e.g., “offensive content”).</p>
     *
     * @param status the current status of the flag (e.g., "PENDING")
     * @param reason the reason string to filter by (e.g., "spam")
     * @return a list of {@link ReviewFlag} entities matching the given criteria
     */
    List<ReviewFlag> findByStatusAndReasonOrderByCreatedAtDesc(String status, String reason);

    /**
     * Retrieves all review flags whose reason field contains the specified
     * substring (case-insensitive), filtered by status and sorted by creation date.
     *
     * <p>Supports fuzzy searching of reasons (e.g., “false info”, “mislead”).</p>
     *
     * @param status the flag status (e.g., "PENDING")
     * @param reasonPart a substring to match within the reason field
     * @return a list of matching {@link ReviewFlag} entities
     */
    List<ReviewFlag> findByStatusAndReasonContainingIgnoreCaseOrderByCreatedAtDesc(String status, String reasonPart);

    /**
     * Retrieves all review flags associated with a specific review ID.
     *
     * <p>Used when displaying all user reports related to a single review.</p>
     *
     * @param reviewId the ID of the review being queried
     * @return a list of {@link ReviewFlag} entities linked to the given review
     */
    @Query("SELECT rf FROM ReviewFlag rf WHERE rf.review.id = :reviewId")
    List<ReviewFlag> findByReviewId(@Param("reviewId") Long reviewId);

    /**
     * Checks if a specific user has already flagged a particular review.
     *
     * <p>This prevents duplicate flag submissions from the same user
     * and enforces single-flag-per-review behavior.</p>
     *
     * @param reviewId the ID of the review being flagged
     * @param userId the ID of the user attempting to flag it
     * @return {@code true} if the user has already flagged the review, {@code false} otherwise
     */
    @Query("SELECT COUNT(rf) > 0 FROM ReviewFlag rf WHERE rf.review.id = :reviewId AND rf.user.id = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    /**
     * Retrieves a flag record for a specific user-review pair, if it exists.
     *
     * <p>Used when checking whether a user has already flagged a review
     * or for allowing updates to a previous report.</p>
     *
     * @param review the {@link Review} being flagged
     * @param user the {@link Users} who submitted the flag
     * @return an {@link Optional} containing the existing {@link ReviewFlag}, if any
     */
    Optional<ReviewFlag> findByReviewAndUser(Review review, Users user);
}
