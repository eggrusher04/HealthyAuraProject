package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_flags")
public class ReviewFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @JsonIgnore
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user; // User who flagged the review

    @Column(name = "reason", nullable = false, length = 50)
    private String reason; // e.g., "spam", "offensive", "false_information"

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, REVIEWED, RESOLVED, DISMISSED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    @JsonIgnore
    private Users admin; // Admin who reviewed the flag

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // Constructor
    public ReviewFlag() {
        this.createdAt = LocalDateTime.now();
    }

    public ReviewFlag(Review review, Users user, String reason) {
        this();
        this.review = review;
        this.user = user;
        this.reason = reason;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Users getAdmin() { return admin; }
    public void setAdmin(Users admin) { this.admin = admin; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}

