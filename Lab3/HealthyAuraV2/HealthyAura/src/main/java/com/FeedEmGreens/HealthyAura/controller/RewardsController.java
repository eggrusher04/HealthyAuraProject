package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.PointsResponse;
import com.FeedEmGreens.HealthyAura.dto.RewardResponse;
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.entity.Reward;
import com.FeedEmGreens.HealthyAura.service.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    // ---------------- POINTS ENDPOINTS ----------------

    /**  Get user's current points */
    @GetMapping("/me")
    public ResponseEntity<PointsResponse> getPoints() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Points p = rewardsService.getUserPoints(username);

        PointsResponse response = new PointsResponse(
                p.getUser().getUsername(),
                p.getTotalPoints(),
                p.getRedeemedPoints(),
                p.getLastUpdated() != null ? p.getLastUpdated().toString() : null
        );

        return ResponseEntity.ok(response);
    }

    /** Add points to a user  */
    @PostMapping("/me/add")
    public ResponseEntity<PointsResponse> addPoints(@RequestParam int points) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Points p = rewardsService.addPoints(username, points);

        PointsResponse response = new PointsResponse(
                p.getUser().getUsername(),
                p.getTotalPoints(),
                p.getRedeemedPoints(),
                p.getLastUpdated().toString()
        );

        return ResponseEntity.ok(response);
    }

    /** Redeem generic points (no specific reward) */
    @PostMapping("/me/redeem")
    public ResponseEntity<PointsResponse> redeemPoints(@RequestParam int points) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Points p = rewardsService.redeemPoints(username, points);

        PointsResponse response = new PointsResponse(
                p.getUser().getUsername(),
                p.getTotalPoints(),
                p.getRedeemedPoints(),
                p.getLastUpdated().toString()
        );

        return ResponseEntity.ok(response);

    }

    // ---------------- REWARD CATALOG ENDPOINTS ----------------

    /**  Get list of all available rewards */
    @GetMapping("/catalog")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardsService.getAllRewards());
    }

    /** Add a new reward (for admin/demo seeding) */
    @PostMapping("/catalog/add")
    public ResponseEntity<Reward> addReward(@RequestBody Reward reward) {
        return ResponseEntity.ok(rewardsService.addReward(reward));
    }

    /** Redeem a specific reward (deduct points accordingly) */
    @PostMapping("/me/redeem-reward/{rewardId}")
    public ResponseEntity<RewardResponse> redeemReward(
            @PathVariable Long rewardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        RewardResponse response = rewardsService.redeemReward(username, rewardId);
        return ResponseEntity.ok(response);
    }


}
