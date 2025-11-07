package com.FeedEmGreens.HealthyAura.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a user's reward points balance within the HealthyAura system.
 *
 * <p>Each {@code Points} record tracks the accumulated, redeemed, and available
 * reward points of a user. Points are earned through actions such as writing reviews,
 * engaging with the platform, or participating in health-related activities,
 * and can be redeemed for rewards or discounts.</p>
 *
 * <p>Entries are stored in the <b>points</b> table, linked one-to-one with a {@link Users} record.</p>
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>Updating total points when a user posts a review</li>
 *   <li>Redeeming points for a reward through the RewardsController</li>
 *   <li>Displaying the user’s current balance on their profile</li>
 * </ul>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 * @see com.FeedEmGreens.HealthyAura.controller.RewardsController
 * @see com.FeedEmGreens.HealthyAura.dto.PointsResponse
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "points")
public class Points {

    /** Unique identifier for the points record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user associated with this points record.
     * <p>Each user has exactly one {@code Points} record, forming a one-to-one relationship.</p>
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private Users user;

    /** The total number of points the user currently holds (including unredeemed). */
    @Column(nullable = false)
    private int totalPoints = 0;

    /** The cumulative number of points the user has redeemed. */
    @Column(nullable = false)
    private int redeemedPoints = 0;

    /** The timestamp when the user’s points balance was last updated. */
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    /** Default constructor for JPA. Initializes default values. */
    public Points() {
        this.totalPoints = 0;
        this.redeemedPoints = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Constructs a {@code Points} entity for a specific user with default values.
     *
     * @param user the {@link Users} entity associated with this points record
     */
    public Points(Users user) {
        this.user = user;
        this.totalPoints = 0;
        this.redeemedPoints = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    /** @return the unique identifier for this points record */
    public Long getId() { return id; }

    /** @return the {@link Users} entity linked to this points record */
    public Users getUser() { return user; }

    /** @param user sets the associated {@link Users} entity */
    public void setUser(Users user) { this.user = user; }

    /** @return the total points currently available to the user */
    public int getTotalPoints() { return totalPoints; }

    /** @param totalPoints sets the user's total points balance */
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    /** @return the total number of points redeemed by the user */
    public int getRedeemedPoints() { return redeemedPoints; }

    /** @param redeemedPoints sets the total number of points redeemed by the user */
    public void setRedeemedPoints(int redeemedPoints) { this.redeemedPoints = redeemedPoints; }

    /** @return the timestamp of the last points update */
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    /** @param lastUpdated sets the last updated timestamp */
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
