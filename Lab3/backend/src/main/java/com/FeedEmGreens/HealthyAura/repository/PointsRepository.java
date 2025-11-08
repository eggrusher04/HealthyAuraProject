package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD and custom query operations
 * on {@link com.FeedEmGreens.HealthyAura.entity.Points} entities.
 *
 * <p>This repository handles data access related to user points — including
 * retrieval, accumulation, and redemption within the HealthyAura rewards system.
 * It provides a convenient way to fetch point balances linked to individual users.</p>
 *
 * <p>Spring Data JPA automatically generates the implementation for this interface,
 * providing built-in CRUD operations such as {@code save()}, {@code findAll()},
 * and {@code deleteById()}, in addition to the custom method defined below.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {

    /**
     * Retrieves the points record associated with a specific user.
     *
     * <p>This method is commonly used when displaying a user’s profile or
     * calculating their remaining redeemable points. Returns an
     * {@link Optional} to handle cases where the user has not yet accumulated
     * any points record.</p>
     *
     * @param user the {@link Users} entity whose points are being retrieved
     * @return an {@link Optional} containing the user's {@link Points} record,
     *         or empty if none exists
     */
    Optional<Points> findByUser(Users user);
}
