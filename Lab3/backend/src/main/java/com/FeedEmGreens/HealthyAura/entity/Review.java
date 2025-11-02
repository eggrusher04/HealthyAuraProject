package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_id", nullable = false)
    @JsonIgnore
    private Eatery eatery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;

    @Column(name = "health_score", nullable = false)
    private Integer healthScore; // 1-5 scale

    @Column(name = "hygiene_score", nullable = false)
    private Integer hygieneScore; // 1-5 scale

    @Column(name = "text_feedback", columnDefinition = "TEXT")
    private String textFeedback;

    @ElementCollection
    @CollectionTable(name = "review_photos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>(); // up to 3 photos

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false;

    @Column(name = "hidden_at")
    private LocalDateTime hiddenAt;

    @Column(name = "hidden_reason", length = 255)
    private String hiddenReason;

    @Column(name = "moderated_by_admin", length = 100)
    private String moderatedByAdminUsername;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_submission_date") // track for 7 days cooldown
    private LocalDateTime lastSubmissionDate;

    // Constructor
    public Review() {
        this.createdAt = LocalDateTime.now();
        this.lastSubmissionDate = LocalDateTime.now();
    }

    public Review(Eatery eatery, Users user, Integer healthScore, Integer hygieneScore) {
        this();
        this.eatery = eatery;
        this.user = user;
        this.healthScore = healthScore;
        this.hygieneScore = hygieneScore;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (lastSubmissionDate == null) {
            lastSubmissionDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // helper methods
    public void addPhoto(String photoUrl) {
        if (photos.size() < 3) {
            photos.add(photoUrl);
        }
    }

    public boolean canSubmitNewReview() {
        if (lastSubmissionDate == null) return true;
        return LocalDateTime.now().isAfter(lastSubmissionDate.plusDays(7));
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Eatery getEatery() { return eatery; }
    public void setEatery(Eatery eatery) { this.eatery = eatery; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { 
        if (healthScore != null && (healthScore < 1 || healthScore > 5)) {
            throw new IllegalArgumentException("Health score must be between 1 and 5");
        }
        this.healthScore = healthScore; 
    }

    public Integer getHygieneScore() { return hygieneScore; }
    public void setHygieneScore(Integer hygieneScore) { 
        if (hygieneScore != null && (hygieneScore < 1 || hygieneScore > 5)) {
            throw new IllegalArgumentException("Hygiene score must be between 1 and 5");
        }
        this.hygieneScore = hygieneScore; 
    }

    public String getTextFeedback() { return textFeedback; }
    public void setTextFeedback(String textFeedback) { this.textFeedback = textFeedback; }

    public List<String> getPhotos() { return photos; }
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
}

