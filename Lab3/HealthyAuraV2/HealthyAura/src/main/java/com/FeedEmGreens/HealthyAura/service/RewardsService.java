package com.FeedEmGreens.HealthyAura.service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;

// Imports for Points
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.repository.PointsRepository;

// Imports for Rewards
import com.FeedEmGreens.HealthyAura.entity.Reward;
import com.FeedEmGreens.HealthyAura.repository.RewardRepository;
import com.FeedEmGreens.HealthyAura.dto.RewardResponse;

import java.util.List;


@Service
public class RewardsService {

    private final PointsRepository pointsRepository;
    private final UserRepository userRepository;
    private final RewardRepository rewardRepository;


    public RewardsService(PointsRepository pointsRepository, UserRepository userRepository, RewardRepository rewardRepository) {
        this.pointsRepository = pointsRepository;
        this.userRepository = userRepository;
        this.rewardRepository = rewardRepository;
    }

    // Get user's points by username (auto-create if missing)
    public Points getUserPoints(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!currentUser.equals(username)){
            throw new AccessDeniedException("Access Forbidden.");
        }

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

    // Deduct points (can go negative - used when reviews are deleted)
    // Note: This bypasses security check 
    // (as in, we need to deduct the points from the user's points so we did not check the authenicated user matching the requested username) 
    // it is to allow admin operations on it (since admin can deduct points from any user).
    public Points deductPoints(String username, int pointsToDeduct) {
        Points points = getUserPointsInternal(username);
        // Allow negative points as per policy
        points.setTotalPoints(points.getTotalPoints() - pointsToDeduct);
        points.setLastUpdated(LocalDateTime.now());
        return pointsRepository.save(points);
    }

    // Internal method to get user points without security check (for admin operations)
    private Points getUserPointsInternal(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return pointsRepository.findByUser(user)
                .orElseGet(() -> createNewUserPoints(user)); //auto-create if not found
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


    // METHODS FOR REWARDS

    // Get all available rewards
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    // Add a new reward (for admin/demo use)
    public Reward addReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    // Redeem a specific reward using username + rewardId

    // @Transactional so that db rolls back transaction if error occurs
    @Transactional
    public RewardResponse redeemReward(String username, Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        Points updatedPoints = redeemPoints(username, reward.getPointsRequired());
        // Build response
        return new RewardResponse(
                username,
                reward.getName(),
                reward.getDescription(),
                reward.getPointsRequired(),
                updatedPoints.getTotalPoints(),
                "Reward redeemed successfully!"
        );
    }
}
