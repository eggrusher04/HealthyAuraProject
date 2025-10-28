package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    // Get only active rewards
    List<Reward> findByActiveTrue();
    // Find a reward by name (useful for seeding checks)
    Reward findByName(String name);
}
