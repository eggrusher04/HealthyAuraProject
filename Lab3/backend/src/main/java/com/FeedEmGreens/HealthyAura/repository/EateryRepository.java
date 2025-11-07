package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD and custom query operations
 * on {@link com.FeedEmGreens.HealthyAura.entity.Eatery} entities.
 *
 * <p>This repository enables searching, filtering, and retrieving
 * eatery data based on attributes such as dietary tags, postal code,
 * and free-text search queries. It integrates with the
 * {@link com.FeedEmGreens.HealthyAura.entity.DietaryTags} entity for
 * tag-based filtering and supports spatial-related lookups (e.g., duplicate detection).</p>
 *
 * <p>Spring Data JPA automatically provides default CRUD methods like
 * {@code save()}, {@code delete()}, {@code findById()}, and {@code findAll()},
 * while the custom methods below define domain-specific queries for the
 * “HealthyAura” system.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.entity.DietaryTags
 * @see com.FeedEmGreens.HealthyAura.service.EateryService
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface EateryRepository extends JpaRepository<Eatery, Long> {

    /**
     * Retrieves all distinct eateries that have at least one dietary tag
     * matching any value in the provided list.
     *
     * <p>Used for tag-based filtering when users select multiple dietary
     * preferences such as “Vegan”, “Halal”, or “Low-Sugar”.</p>
     *
     * @param tags a list of dietary tag names to match
     * @return a list of distinct {@link Eatery} entities with matching tags
     */
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt WHERE dt.tag IN :tags")
    List<Eatery> findByDietaryTagsIn(@Param("tags") List<String> tags);

    /**
     * Retrieves all distinct eateries that match a single dietary tag.
     *
     * <p>This method supports searches like “find all Halal eateries”.</p>
     *
     * @param tag a dietary tag name to search for
     * @return a list of distinct {@link Eatery} entities with the given tag
     */
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt WHERE dt.tag = :tag")
    List<Eatery> findByDietaryTag(@Param("tag") String tag);

    /**
     * Finds eateries by their registered postal code.
     *
     * <p>Useful for geolocation-based filtering or map-centric searches
     * (e.g., “show all eateries in postal code 640123”).</p>
     *
     * @param postalCode the postal code of interest
     * @return a list of {@link Eatery} entities with the specified postal code
     */
    List<Eatery> findByPostalCode(Long postalCode);

    /**
     * Finds eateries by exact name and coordinates (latitude and longitude).
     *
     * <p>This query helps prevent duplicate entries when importing or
     * registering eateries via admin interfaces.</p>
     *
     * @param name the eatery’s name
     * @param latitude the eatery’s latitude coordinate
     * @param longitude the eatery’s longitude coordinate
     * @return a list of {@link Eatery} entities matching the same name and coordinates
     */
    List<Eatery> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude);

    /**
     * Performs a flexible keyword search across multiple eatery fields
     * (name, building name, address, and postal code).
     *
     * <p>Supports partial and case-insensitive matches for user queries in
     * the front-end search bar.</p>
     *
     * @param query a free-text search query
     * @return a list of {@link Eatery} entities whose name, building name,
     * address, or postal code contains the given query string
     */
    @Query("SELECT e FROM Eatery e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.buildingName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(e.postalCode AS string) LIKE CONCAT('%', :query, '%')")
    List<Eatery> searchByQuery(@Param("query") String query);
}
