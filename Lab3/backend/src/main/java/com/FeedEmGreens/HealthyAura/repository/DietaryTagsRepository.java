package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.DietaryTags;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietaryTagsRepository extends JpaRepository<DietaryTags, Long> {
    List<DietaryTags> findByEatery(Eatery eatery);
    Optional<DietaryTags> findByEateryAndTagIgnoreCase(Eatery eatery, String tag);
}


