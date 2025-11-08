package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an administrative action log entry.
 *
 * <p>This class is used to record audit trails of admin activities,
 * such as moderation actions, tag edits, and data updates within
 * the HealthyAura system. Each record stores the type of action,
 * target entity details, timestamp, and optional descriptive information.</p>
 *
 * <p>Entries are stored in the <b>admin_action_logs</b> database table.</p>
 *
 * <p>Example use cases:</p>
 * <ul>
 *   <li>Logging when an admin adds or removes a tag from an eatery</li>
 *   <li>Recording review moderation actions such as "flag resolved" or "review deleted"</li>
 *   <li>Tracking admin-initiated data edits for audit purposes</li>
 * </ul>
 *
 * @see com.FeedEmGreens.HealthyAura.controller.AdminLogController
 * @see com.FeedEmGreens.HealthyAura.repository.AdminActionLogRepository
 * @see com.FeedEmGreens.HealthyAura.service.ReviewService
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {

    /** Unique identifier for each admin action log entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The username of the admin who performed the action. */
    @Column(name = "admin_username", nullable = false, length = 100)
    private String adminUsername;

    /** The type of action performed (e.g., ADD_TAG, DELETE_TAG, EDIT_TAG). */
    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    /** The type of target entity affected by the action (e.g., TAG, REVIEW, EATERY). */
    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    /** The ID of the specific entity affected by the action (e.g., tag ID, review ID). */
    @Column(name = "target_id")
    private Long targetId;

    /** The ID of the associated eatery, if applicable. */
    @Column(name = "eatery_id")
    private Long eateryId;

    /** Additional details describing the action (e.g., before/after state or remarks). */
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    /** The timestamp when the action occurred. */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /** Default no-argument constructor for JPA. */
    public AdminActionLog() {}

    /**
     * Constructs a new {@code AdminActionLog} with all fields initialized.
     *
     * @param adminUsername the admin’s username who performed the action
     * @param actionType    the type of action performed (e.g., ADD_TAG, DELETE_REVIEW)
     * @param targetType    the type of target entity affected
     * @param targetId      the ID of the target entity
     * @param eateryId      the ID of the related eatery (if applicable)
     * @param details       additional details or description of the action
     * @param timestamp     the time when the action occurred
     */
    public AdminActionLog(String adminUsername, String actionType, String targetType,
                          Long targetId, Long eateryId, String details, LocalDateTime timestamp) {
        this.adminUsername = adminUsername;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.eateryId = eateryId;
        this.details = details;
        this.timestamp = timestamp;
    }

    /** @return the unique log ID */
    public Long getId() { return id; }

    /** @param id sets the log ID */
    public void setId(Long id) { this.id = id; }

    /** @return the admin username who performed the action */
    public String getAdminUsername() { return adminUsername; }

    /** @param adminUsername sets the admin username */
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    /** @return the type of action performed */
    public String getActionType() { return actionType; }

    /** @param actionType sets the type of action (e.g., EDIT_TAG, DELETE_REVIEW) */
    public void setActionType(String actionType) { this.actionType = actionType; }

    /** @return the target entity type affected by the action */
    public String getTargetType() { return targetType; }

    /** @param targetType sets the target entity type (e.g., TAG, REVIEW) */
    public void setTargetType(String targetType) { this.targetType = targetType; }

    /** @return the target entity ID */
    public Long getTargetId() { return targetId; }

    /** @param targetId sets the target entity ID */
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    /** @return the associated eatery ID (if applicable) */
    public Long getEateryId() { return eateryId; }

    /** @param eateryId sets the associated eatery ID */
    public void setEateryId(Long eateryId) { this.eateryId = eateryId; }

    /** @return the details or notes about the action */
    public String getDetails() { return details; }

    /** @param details sets the action’s details or description */
    public void setDetails(String details) { this.details = details; }

    /** @return the timestamp when the action occurred */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @param timestamp sets the time the action was performed */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
