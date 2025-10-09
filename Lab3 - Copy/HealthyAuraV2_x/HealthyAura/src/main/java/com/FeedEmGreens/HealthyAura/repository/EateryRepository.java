package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EateryRepository extends JpaRepository<Eatery, Long> {
    
    // Find eateries by dietary tags
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt WHERE dt.tag IN :tags")
    List<Eatery> findByDietaryTagsIn(@Param("tags") List<String> tags);
    
    // Find eateries by single dietary tag
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt WHERE dt.tag = :tag")
    List<Eatery> findByDietaryTag(@Param("tag") String tag);
    
    // Find eateries by area/building name
    List<Eatery> findByBuildingNameContainingIgnoreCase(String buildingName);
    
    // Find eateries by postal code
    List<Eatery> findByPostalCode(Long postalCode);
    
    /* 
    // Find eateries with minimum rating
    @Query("SELECT e FROM Eatery e WHERE e.aggregateRating >= :minRating")
    List<Eatery> findByMinRating(@Param("minRating") Double minRating);
    
    // Find eateries within price range
    @Query("SELECT e FROM Eatery e WHERE e.priceIndicator BETWEEN :minPrice AND :maxPrice")
    List<Eatery> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);
    
    // Find eateries with low queue time
    @Query("SELECT e FROM Eatery e WHERE e.queueTime <= :maxQueueTime")
    List<Eatery> findByMaxQueueTime(@Param("maxQueueTime") Integer maxQueueTime);
    
    // Find nearby eateries (within radius)
    @Query("SELECT e FROM Eatery e WHERE " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * " +
           "cos(radians(e.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(e.latitude))) <= :radius")
    List<Eatery> findNearbyEateries(@Param("latitude") Double latitude, 
                                   @Param("longitude") Double longitude, 
                                   @Param("radius") Double radius);
    
    // Find eateries by name containing
    List<Eatery> findByNameContainingIgnoreCase(String name);
    
    // Complex query for personalized recommendations
    @Query("SELECT DISTINCT e FROM Eatery e " +
           "LEFT JOIN e.dietaryTags dt " +
           "WHERE (dt.tag IN :preferredTags OR :preferredTags IS NULL) " +
           "AND (e.aggregateRating >= :minRating OR :minRating IS NULL) " +
           "AND (e.priceIndicator <= :maxPrice OR :maxPrice IS NULL) " +
           "AND (e.queueTime <= :maxQueueTime OR :maxQueueTime IS NULL)")
    List<Eatery> findPersonalizedRecommendations(@Param("preferredTags") List<String> preferredTags,
                                                @Param("minRating") Double minRating,
                                                @Param("maxPrice") Integer maxPrice,
                                                @Param("maxQueueTime") Integer maxQueueTime);
    
    // Find top rated eateries
    @Query("SELECT e FROM Eatery e WHERE e.aggregateRating IS NOT NULL ORDER BY e.aggregateRating DESC")
    List<Eatery> findTopRatedEateries();
    
    // Find budget-friendly eateries
    @Query("SELECT e FROM Eatery e WHERE e.priceIndicator <= 2 ORDER BY e.aggregateRating DESC")
    List<Eatery> findBudgetFriendlyEateries();
    
    // Find eateries with specific tags and location
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt " +
           "WHERE dt.tag IN :tags " +
           "AND 6371 * acos(cos(radians(:latitude)) * cos(radians(e.latitude)) * " +
           "cos(radians(e.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(e.latitude))) <= :radius")
    List<Eatery> findByTagsAndLocation(@Param("tags") List<String> tags,
                                      @Param("latitude") Double latitude,
                                      @Param("longitude") Double longitude,
                                      @Param("radius") Double radius);*/
}
