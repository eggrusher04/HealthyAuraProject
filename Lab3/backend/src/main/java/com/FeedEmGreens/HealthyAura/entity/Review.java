package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user-submitted review for an {@link Eatery} in the HealthyAura system.
 *
 * <p>Each {@code Review} record captures a user’s assessment of an eatery’s
 * health and hygiene standards, textual feedback, and optional photo uploads.
 * Reviews contribute to the platform’s transparency, user engagement, and
 * recommendation scoring algorithms.</p>
 *
 * <p>This entity also includes moderation metadata, allowing administrators
 * to hide, delete, or review flagged submissions.</p>
 *
 * <p>Entries are stored in the <b>reviews</b> table, with photo URLs stored in a
 * separate <b>review_photos</b> collection table.</p>
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>Users submit or update reviews for eateries</li>
 *   <li>Admins moderate or hide inappropriate content</li>
 *   <li>System awards points to users for eligible reviews</li>
 * </ul>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.dto.ReviewRequest
 * @see com.FeedEmGreens.HealthyAura.dto.ReviewResponse
 * @see com.FeedEmGreens.HealthyAura.controller.ReviewController
 * @see com.FeedEmGreens.HealthyAura.service.ReviewService
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "reviews")
public class Review {

    /** Unique identifier for the review. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The eatery that this review is associated with. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_id", nullable = false)
    @JsonIgnore
    private Eatery eatery;

    /** The user who submitted this review. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;

    /** The eatery's health rating given by the user (1–5 scale). */
    @Column(name = "health_score", nullable = false)
    private Integer healthScore;

    /** The eatery's hygiene rating given by the user (1–5 scale). */
    @Column(name = "hygiene_score", nullable = false)
    private Integer hygieneScore;

    /** User-provided textual feedback describing their experience. */
    @Column(name = "text_feedback", columnDefinition = "TEXT")
    private String textFeedback;

    /**
     * A list of photo URLs attached to the review.
     * <p>Maximum of 3 photos are allowed per review.</p>
     */
    @ElementCollection
    @CollectionTable(name = "review_photos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    /** Flag indicating if the review was deleted by the user or system. */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /** Flag indicating if the review is hidden by an admin (e.g., due to policy violations). */
    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false;

    /** Timestamp marking when the review was hidden. */
    @Column(name = "hidden_at")
    private LocalDateTime hiddenAt;

    /** Reason for hiding the review, set by an admin. */
    @Column(name = "hidden_reason", length = 255)
    private String hiddenReason;

    /** Username of the admin who moderated or hid this review. */
    @Column(name = "moderated_by_admin", length = 100)
    private String moderatedByAdminUsername;

    /** Timestamp when the review was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the review was last updated. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Date of last submission to enforce 7-day cooldown between updates. */
    @Column(name = "last_submission_date")
    private LocalDateTime lastSubmissionDate;

    /** Points awarded for this review, used for later deductions if deleted. */
    @Column(name = "points_awarded")
    private Integer pointsAwarded = 0;

    /** Default constructor initializing timestamps. */
    public Review() {
        this.createdAt = LocalDateTime.now();
        this.lastSubmissionDate = LocalDateTime.now();
    }

    /**
     * Constructs a {@code Review} for a given eatery and user with specified scores.
     *
     * @param eatery the {@link Eatery} being reviewed
     * @param user the {@link Users} who submitted the review
     * @param healthScore health rating (1–5)
     * @param hygieneScore hygiene rating (1–5)
     */
    public Review(Eatery eatery, Users user, Integer healthScore, Integer hygieneScore) {
        this();
        this.eatery = eatery;
        this.user = user;
        this.healthScore = healthScore;
        this.hygieneScore = hygieneScore;
    }

    /** Lifecycle callback to initialize timestamps before persisting a new review. */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (lastSubmissionDate == null) {
            lastSubmissionDate = LocalDateTime.now();
        }
    }

    /** Lifecycle callback to update the modification timestamp on updates. */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a new photo URL to the review, enforcing a maximum of 3 photos.
     *
     * @param photoUrl the URL of the photo to add
     */
    public void addPhoto(String photoUrl) {
        if (photos.size() < 3) {
            photos.add(photoUrl);
        }
    }

    /**
     * Checks whether the user can submit a new review, enforcing a 7-day cooldown.
     *
     * @return {@code true} if the user can post a new review, otherwise {@code false}
     */
    public boolean canSubmitNewReview() {
        if (lastSubmissionDate == null) return true;
        return LocalDateTime.now().isAfter(lastSubmissionDate.plusDays(7));
    }

    // Getters and Setters with validation where applicable

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Eatery getEatery() { return eatery; }
    public void setEatery(Eatery eatery) { this.eatery = eatery; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Integer getHealthScore() { return healthScore; }

    /**
     * Sets the health score, validating it within the range 1–5.
     *
     * @param healthScore the health score
     * @throws IllegalArgumentException if the score is not between 1 and 5
     */
    public void setHealthScore(Integer healthScore) {
        if (healthScore != null && (healthScore < 1 || healthScore > 5)) {
            throw new IllegalArgumentException("Health score must be between 1 and 5");
        }
        this.healthScore = healthScore;
    }

    public Integer getHygieneScore() { return hygieneScore; }

    /**
     * Sets the hygiene score, validating it within the range 1–5.
     *
     * @param hygieneScore the hygiene score
     * @throws IllegalArgumentException if the score is not between 1 and 5
     */
    public void setHygieneScore(Integer hygieneScore) {
        if (hygieneScore != null && (hygieneScore < 1 || hygieneScore > 5)) {
            throw new IllegalArgumentException("Hygiene score must be between 1 and 5");
        }
        this.hygieneScore = hygieneScore;
    }

    public String getTextFeedback() { return textFeedback; }
    public void setTextFeedback(String textFeedback) { this.textFeedback = textFeedback; }

    public List<String> getPhotos() { return photos; }

    /**
     * Sets the list of photos, enforcing a maximum of 3.
     *
     * @param photos the list of photo URLs
     * @throws IllegalArgumentException if more than 3 photos are provided
     */
    public void setPhotos(List<String> photos) {
        if (photos != null && photos.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 photos allowed");
        }
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }

    public LocalDateTime getHiddenAt() { return hiddenAt; }
    public void setHiddenAt(LocalDateTime hiddenAt) { this.hiddenAt = hiddenAt; }

    public String getHiddenReason() { return hiddenReason; }
    public void setHiddenReason(String hiddenReason) { this.hiddenReason = hiddenReason; }

    public String getModeratedByAdminUsername() { return moderatedByAdminUsername; }
    public void setModeratedByAdminUsername(String moderatedByAdminUsername) { this.moderatedByAdminUsername = moderatedByAdminUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastSubmissionDate() { return lastSubmissionDate; }
    public void setLastSubmissionDate(LocalDateTime lastSubmissionDate) { this.lastSubmissionDate = lastSubmissionDate; }

    public Integer getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(Integer pointsAwarded) { this.pointsAwarded = pointsAwarded; }
}
