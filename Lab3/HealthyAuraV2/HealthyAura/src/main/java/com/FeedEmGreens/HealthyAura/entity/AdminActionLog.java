package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_username", nullable = false, length = 100)
    private String adminUsername;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // e.g., ADD_TAG, DELETE_TAG, EDIT_TAG

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType; // e.g., TAG

    @Column(name = "target_id")
    private Long targetId; // e.g., tag id or eatery id as needed

    @Column(name = "eatery_id")
    private Long eateryId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // outcome or before/after info

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AdminActionLog() {}

    public AdminActionLog(String adminUsername, String actionType, String targetType, Long targetId, Long eateryId, String details, LocalDateTime timestamp) {
        this.adminUsername = adminUsername;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.eateryId = eateryId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Long getEateryId() { return eateryId; }
    public void setEateryId(Long eateryId) { this.eateryId = eateryId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}


