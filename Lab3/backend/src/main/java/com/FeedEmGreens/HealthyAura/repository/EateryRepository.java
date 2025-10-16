package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EateryRepository extends JpaRepository<Eatery, Long> {
    
    // Find eateries by dietary tags
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt WHERE dt.tag IN :tags")
    List<Eatery> findByDietaryTagsIn(@Param("tags") List<String> tags);
    
    // Find eateries by single dietary tag
    @Query("SELECT DISTINCT e FROM Eatery e JOIN e.dietaryTags dt WHERE dt.tag = :tag")
    List<Eatery> findByDietaryTag(@Param("tag") String tag);
    
    // Find eateries by postal code
    List<Eatery> findByPostalCode(Long postalCode);
    
    // Find eatery by name and coordinates (for duplicate checking)
    List<Eatery> findByNameAndLatitudeAndLongitude(String name, Double latitude, Double longitude);

}
