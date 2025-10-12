package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.repository.PointsRepository;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RewardsService {

    private final PointsRepository pointsRepository;
    private final UserRepository userRepository;

    public RewardsService(PointsRepository pointsRepository, UserRepository userRepository) {
        this.pointsRepository = pointsRepository;
        this.userRepository = userRepository;
    }

    // Get user's points by username (auto-create if missing)
    public Points getUserPoints(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return pointsRepository.findByUser(user)
                .orElseGet(() -> createNewUserPoints(user)); //auto-create if not found
    }

    // Add points
    public Points addPoints(String username, int pointsToAdd) {
        Points points = getUserPoints(username);
        points.setTotalPoints(points.getTotalPoints() + pointsToAdd);
        points.setLastUpdated(LocalDateTime.now());
        return pointsRepository.save(points);
    }

    // Redeem points
    public Points redeemPoints(String username, int pointsToRedeem) {
        Points points = getUserPoints(username);

        if (points.getTotalPoints() < pointsToRedeem) {
            throw new RuntimeException("Not enough points to redeem");
        }

        points.setTotalPoints(points.getTotalPoints() - pointsToRedeem);
        points.setRedeemedPoints(points.getRedeemedPoints() + pointsToRedeem);
        points.setLastUpdated(LocalDateTime.now());

        return pointsRepository.save(points);
    }

    // Helper method for initializing new users' points
    private Points createNewUserPoints(Users user) {
        Points newPoints = new Points();
        newPoints.setUser(user);
        newPoints.setTotalPoints(0);
        newPoints.setRedeemedPoints(0);
        newPoints.setLastUpdated(LocalDateTime.now());
        return pointsRepository.save(newPoints);
    }
}
