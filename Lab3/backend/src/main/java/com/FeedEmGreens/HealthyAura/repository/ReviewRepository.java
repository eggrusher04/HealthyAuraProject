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

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find visible reviews for an eatery (non-deleted and not hidden)
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false ORDER BY r.createdAt DESC")
    List<Review> findVisibleByEateryOrderByCreatedAtDesc(@Param("eatery") Eatery eatery);
    
    // Find user's review for a specific eatery (active only)
    Optional<Review> findByEateryAndUserAndIsDeletedFalse(Eatery eatery, Users user);
    
    // Find all reviews by a user
    List<Review> findByUserAndIsDeletedFalse(Users user);
    
    // Find all active reviews (for moderation)
    List<Review> findByIsDeletedFalse();
    
    // Aggregate queries for ratings
    @Query("SELECT AVG(r.healthScore) FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false")
    Double getAverageHealthScore(@Param("eatery") Eatery eatery);
    
    @Query("SELECT AVG(r.hygieneScore) FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false")
    Double getAverageHygieneScore(@Param("eatery") Eatery eatery);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false")
    Long getReviewCount(@Param("eatery") Eatery eatery);
    
    // Check if user has submitted a review within the last 7 days (including deleted ones, to prevent bypass)
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.user = :user " +
           "AND r.lastSubmissionDate >= :sevenDaysAgo")
    List<Review> findRecentSubmissions(@Param("eatery") Eatery eatery, 
                                       @Param("user") Users user,
                                       @Param("sevenDaysAgo") java.time.LocalDateTime sevenDaysAgo);
    
    // Find reviews sorted by highest health score
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false " +
           "ORDER BY r.healthScore DESC, r.createdAt DESC")
    List<Review> findByEateryOrderByHealthScoreDesc(@Param("eatery") Eatery eatery);
    
    // Find reviews sorted by highest hygiene score
    @Query("SELECT r FROM Review r WHERE r.eatery = :eatery AND r.isDeleted = false AND r.isHidden = false " +
           "ORDER BY r.hygieneScore DESC, r.createdAt DESC")
    List<Review> findByEateryOrderByHygieneScoreDesc(@Param("eatery") Eatery eatery);
    
    // Count reviews created by user today (for global daily limit - max 5 reviews/day)
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user = :user " +
           "AND r.createdAt >= :startOfDay")
    Long countReviewsCreatedTodayByUser(@Param("user") Users user, 
                                        @Param("startOfDay") java.time.LocalDateTime startOfDay);
}

