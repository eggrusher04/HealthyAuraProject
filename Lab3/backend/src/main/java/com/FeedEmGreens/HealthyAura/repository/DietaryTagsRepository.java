package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.DietaryTags;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD and query operations on
 * {@link com.FeedEmGreens.HealthyAura.entity.DietaryTags} entities.
 *
 * <p>This repository manages dietary tags linked to individual eateries,
 * supporting tag-based categorization for recommendation, filtering, and
 * admin management features.</p>
 *
 * <p>Spring Data JPA automatically implements this interface, providing
 * standard CRUD methods such as {@code save()}, {@code delete()}, and {@code findAll()},
 * along with custom finder methods defined below.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.DietaryTags
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.repository.EateryRepository
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface DietaryTagsRepository extends JpaRepository<DietaryTags, Long> {

    /**
     * Retrieves all dietary tags associated with a specific eatery.
     *
     * <p>This method is typically used when displaying or editing the list of
     * tags attached to a particular eatery within the application.</p>
     *
     * @param eatery the {@link Eatery} entity whose tags are to be retrieved
     * @return a list of {@link DietaryTags} linked to the given eatery
     */
    List<DietaryTags> findByEatery(Eatery eatery);

    /**
     * Retrieves a dietary tag for a specific eatery by its tag name,
     * ignoring case sensitivity.
     *
     * <p>This query is commonly used to prevent duplicate tag entries
     * (e.g., "Vegan" and "vegan") during tag creation or updates.</p>
     *
     * @param eatery the {@link Eatery} entity the tag belongs to
     * @param tag the dietary tag name (case-insensitive)
     * @return an {@link Optional} containing the {@link DietaryTags} if found,
     *         or empty if the tag does not exist for that eatery
     */
    Optional<DietaryTags> findByEateryAndTagIgnoreCase(Eatery eatery, String tag);
}
