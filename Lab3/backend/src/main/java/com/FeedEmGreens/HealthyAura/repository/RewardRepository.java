package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD and custom query operations
 * on {@link com.FeedEmGreens.HealthyAura.entity.Reward} entities.
 *
 * <p>This repository provides methods to manage and retrieve reward-related
 * data for the HealthyAura rewards and gamification system. Rewards represent
 * tangible or virtual benefits that users can redeem using points accumulated
 * through healthy activity, reviews, or engagement.</p>
 *
 * <p>Spring Data JPA automatically implements common data operations such as
 * {@code save()}, {@code findById()}, and {@code deleteById()}, while the
 * custom query methods below extend its functionality for domain-specific logic.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Reward
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    /**
     * Retrieves all rewards that are currently marked as active.
     *
     * <p>Used to display available rewards in the user-facing catalog,
     * ensuring that deactivated or expired rewards are excluded from
     * redemption options.</p>
     *
     * @return a list of {@link Reward} entities where {@code active = true}
     */
    List<Reward> findByActiveTrue();

    /**
     * Finds a reward by its exact name.
     *
     * <p>This method is primarily used for validation during database seeding
     * or administrative creation of rewards to prevent duplicates.</p>
     *
     * @param name the name of the reward
     * @return the {@link Reward} entity with the specified name, or {@code null} if not found
     */
    Reward findByName(String name);
}
