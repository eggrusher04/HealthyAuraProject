package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Review;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD and custom query operations
 * on {@link com.FeedEmGreens.HealthyAura.entity.Review} entities.
 *
 * <p>This repository manages all database interactions related to user reviews,
 * including fetching, filtering, moderation, and aggregation of ratings data.
 * It supports both public-facing review retrieval and backend moderation or
 * analytics functionality for administrators.</p>
 *
 * <p>Spring Data JPA automatically provides default CRUD methods such as
 * {@code findAll()}, {@code findById()}, {@code save()}, and {@code deleteById()},
 * while the custom query methods defined here extend its functionality for
 * HealthyAura’s review and reputation system.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Review
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.service.ReviewService
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Retrieves all visible (non-deleted and not hidden) reviews for a given eatery,
     * sorted by creation date in descending order.
     *
     * <p>This method is primarily used for displaying recent public reviews
     * to end-users.</p>
     *
     * @param eatery the {@link Eatery} entity for which reviews are retrieved
     * @return a list of visible {@link Review} entities ordered by newest first
     */
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false ORDER BY r.createdAt DESC")
    List<Review> findVisibleByEateryOrderByCreatedAtDesc(@Param("eatery") Eatery eatery);

    /**
     * Finds a user’s active (non-deleted) review for a specific eatery.
     *
     * <p>This is used to enforce the one-review-per-eatery policy
     * or for editing an existing review.</p>
     *
     * @param eatery the eatery being reviewed
     * @param user the user who submitted the review
     * @return an {@link Optional} containing the review if found
     */
    Optional<Review> findByEateryAndUserAndIsDeletedFalse(Eatery eatery, Users user);

    /**
     * Retrieves all active (non-deleted) reviews submitted by a given user.
     *
     * @param user the {@link Users} entity whose reviews are being fetched
     * @return a list of the user’s visible {@link Review} entries
     */
    List<Review> findByUserAndIsDeletedFalse(Users user);

    /**
     * Retrieves all active (non-deleted) reviews in the database.
     *
     * <p>Primarily used by admins for moderation and system maintenance tasks.</p>
     *
     * @return a list of active {@link Review} entities
     */
    List<Review> findByIsDeletedFalse();

    /**
     * Calculates the average health score for a specific eatery,
     * considering only visible (non-deleted and non-hidden) reviews.
     *
     * @param eatery the eatery whose health rating is being averaged
     * @return the average health score as a {@link Double}, or {@code null} if no reviews exist
     */
    @Query("SELECT AVG(r.healthScore) FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false")
    Double getAverageHealthScore(@Param("eatery") Eatery eatery);

    /**
     * Calculates the average hygiene score for a specific eatery,
     * considering only visible (non-deleted and non-hidden) reviews.
     *
     * @param eatery the eatery whose hygiene rating is being averaged
     * @return the average hygiene score as a {@link Double}, or {@code null} if no reviews exist
     */
    @Query("SELECT AVG(r.hygieneScore) FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false")
    Double getAverageHygieneScore(@Param("eatery") Eatery eatery);

    /**
     * Counts the total number of visible (non-deleted and non-hidden) reviews for an eatery.
     *
     * <p>Used for displaying the total review count in recommendation and analytics modules.</p>
     *
     * @param eatery the {@link Eatery} being queried
     * @return the number of reviews as a {@link Long}
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false")
    Long getReviewCount(@Param("eatery") Eatery eatery);

    /**
     * Retrieves all reviews submitted by a specific user for a given eatery
     * within the last seven days (including deleted ones).
     *
     * <p>This helps enforce the review submission cooldown policy,
     * preventing repeated spam submissions.</p>
     *
     * @param eatery the eatery being reviewed
     * @param user the user who submitted the review
     * @param sevenDaysAgo the date threshold (typically {@code LocalDateTime.now().minusDays(7)})
     * @return a list of recent {@link Review} entities within the cooldown period
     */
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.user = :user " +
            "AND r.lastSubmissionDate >= :sevenDaysAgo")
    List<Review> findRecentSubmissions(@Param("eatery") Eatery eatery,
                                       @Param("user") Users user,
                                       @Param("sevenDaysAgo") java.time.LocalDateTime sevenDaysAgo);

    /**
     * Retrieves reviews for an eatery sorted by the highest health score,
     * followed by most recent submissions.
     *
     * @param eatery the {@link Eatery} being queried
     * @return a list of {@link Review} entities sorted by descending health score
     */
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false " +
            "ORDER BY r.healthScore DESC, r.createdAt DESC")
    List<Review> findByEateryOrderByHealthScoreDesc(@Param("eatery") Eatery eatery);

    /**
     * Retrieves reviews for an eatery sorted by the highest hygiene score,
     * followed by most recent submissions.
     *
     * @param eatery the {@link Eatery} being queried
     * @return a list of {@link Review} entities sorted by descending hygiene score
     */
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false " +
            "ORDER BY r.hygieneScore DESC, r.createdAt DESC")
    List<Review> findByEateryOrderByHygieneScoreDesc(@Param("eatery") Eatery eatery);

    /**
     * Counts the number of reviews created by a user since the start of the day.
     *
     * <p>This supports a global daily review limit (e.g., 5 reviews/day)
     * to prevent spam and ensure fair system usage.</p>
     *
     * @param user the {@link Users} entity being checked
     * @param startOfDay the start of the current day (e.g., midnight timestamp)
     * @return the number of reviews submitted by the user today
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user = :user " +
            "AND r.createdAt >= :startOfDay")
    Long countReviewsCreatedTodayByUser(@Param("user") Users user,
                                        @Param("startOfDay") java.time.LocalDateTime startOfDay);
}
