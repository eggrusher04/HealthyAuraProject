package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;

/**
 * Entity representing a redeemable reward in the HealthyAura reward system.
 *
 * <p>Each {@code Reward} record defines a reward item that users can redeem using
 * their accumulated points. Rewards may include discounts, free items, or vouchers
 * and can be activated or deactivated by administrators.</p>
 *
 * <p>Entries are stored in the <b>reward</b> table, and managed through
 * the {@link com.FeedEmGreens.HealthyAura.controller.RewardsController}.</p>
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>Admin seeds or adds rewards (e.g., “Free Salad Bowl”)</li>
 *   <li>Users redeem rewards through their available points</li>
 *   <li>Inactive rewards are hidden from the catalog</li>
 * </ul>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 * @see com.FeedEmGreens.HealthyAura.dto.RewardResponse
 * @see com.FeedEmGreens.HealthyAura.controller.RewardsController
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
public class Reward {

    /** Unique identifier for the reward. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The name or title of the reward (e.g., "Free Healthy Drink"). */
    private String name;

    /** A brief description of what the reward offers. */
    private String description;

    /** The number of points required to redeem this reward. */
    private int pointsRequired;

    /** Indicates whether this reward is active and available for redemption. */
    private boolean active = true;

    /** Default constructor required by JPA. */
    public Reward() {}

    /**
     * Constructs a new {@code Reward} with the specified name, description, and points required.
     *
     * @param name the display name of the reward
     * @param description a brief explanation of the reward
     * @param pointsRequired the number of points needed to redeem this reward
     */
    public Reward(String name, String description, int pointsRequired) {
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
    }

    /** @return the unique ID of the reward */
    public Long getId() { return id; }

    /** @return the name of the reward */
    public String getName() { return name; }

    /** @param name sets the name of the reward */
    public void setName(String name) { this.name = name; }

    /** @return the description of the reward */
    public String getDescription() { return description; }

    /** @param description sets a brief explanation for the reward */
    public void setDescription(String description) { this.description = description; }

    /** @return the number of points required to redeem the reward */
    public int getPointsRequired() { return pointsRequired; }

    /** @param pointsRequired sets the number of points needed to redeem the reward */
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }

    /** @return {@code true} if the reward is active and available for redemption */
    public boolean isActive() { return active; }

    /** @param active sets whether the reward is active and available */
    public void setActive(boolean active) { this.active = active; }
}
