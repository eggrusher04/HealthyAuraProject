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

@Repository
public interface ReviewFlagRepository extends JpaRepository<ReviewFlag, Long> {
    
    // Find all pending flags (for admin moderation queue)
    List<ReviewFlag> findByStatusOrderByCreatedAtDesc(String status);

    // Find pending flags by specific reason (e.g., hygiene/health)
    List<ReviewFlag> findByStatusAndReasonOrderByCreatedAtDesc(String status, String reason);

    // Find pending flags where reason contains substring (case-insensitive)
    List<ReviewFlag> findByStatusAndReasonContainingIgnoreCaseOrderByCreatedAtDesc(String status, String reasonPart);
    
    // Find flags for a specific review
    @Query("SELECT rf FROM ReviewFlag rf WHERE rf.review.id = :reviewId")
    List<ReviewFlag> findByReviewId(@Param("reviewId") Long reviewId);
    
    // Check if user has already flagged a review
    @Query("SELECT COUNT(rf) > 0 FROM ReviewFlag rf WHERE rf.review.id = :reviewId AND rf.user.id = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    // Find by review and user entities
    Optional<ReviewFlag> findByReviewAndUser(Review review, Users user);
}

