package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

/**
 * Entity representing a user-submitted flag (report) on a specific {@link Review}.
 *
 * <p>Each {@code ReviewFlag} record is created when a user reports a review as inappropriate,
 * misleading, or otherwise violating platform guidelines. Admins can later resolve these
 * flags by reviewing the reported content and marking them as resolved or dismissed.</p>
 *
 * <p>This entity plays a key role in moderation and admin dashboards, supporting workflows like:</p>
 * <ul>
 *   <li>Users flagging suspicious or offensive reviews</li>
 *   <li>Admins reviewing flagged reports in the moderation queue</li>
 *   <li>Tracking resolution outcomes with timestamps and admin notes</li>
 * </ul>
 *
 * <p>Entries are stored in the <b>review_flags</b> table.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Review
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.controller.AdminReviewModerationController
 * @see com.FeedEmGreens.HealthyAura.repository.ReviewFlagRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "review_flags")
public class ReviewFlag {

    /** Unique identifier for the review flag entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The review that was flagged by a user. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @JsonIgnore
    private Review review;

    /** The user who submitted the flag report. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;

    /**
     * The reason for flagging the review.
     * <p>Examples include: "spam", "offensive", "false_information", "hygiene", or "health".</p>
     */
    @Column(name = "reason", nullable = false, length = 50)
    private String reason;

    /**
     * The moderation status of the flag.
     * <p>Possible values:
     * <ul>
     *   <li><b>PENDING</b> — waiting for admin review</li>
     *   <li><b>RESOLVED</b> — admin confirmed and acted (e.g., review removed)</li>
     *   <li><b>DISMISSED</b> — admin found flag invalid</li>
     * </ul>
     * </p>
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    /** Timestamp when the flag was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** The admin who handled this flag report, if resolved or dismissed. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    @JsonIgnore
    private Users admin;

    /** Notes or remarks provided by the admin during moderation. */
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    /** Timestamp marking when the flag was reviewed by an admin. */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /** Default constructor initializing creation timestamp. */
    public ReviewFlag() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructs a new {@code ReviewFlag} for a given review and reporting user.
     *
     * @param review the {@link Review} being flagged
     * @param user the {@link Users} who submitted the report
     * @param reason the reason for flagging
     */
    public ReviewFlag(Review review, Users user, String reason) {
        this();
        this.review = review;
        this.user = user;
        this.reason = reason;
    }

    /** Lifecycle callback for setting timestamps before persistence. */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ---------------- GETTERS AND SETTERS ---------------- //

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
