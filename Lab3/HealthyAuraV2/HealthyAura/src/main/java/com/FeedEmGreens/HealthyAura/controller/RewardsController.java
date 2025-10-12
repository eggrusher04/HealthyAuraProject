package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.PointsResponse;
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.service.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<PointsResponse> getPoints(@PathVariable String username) {
        Points p = rewardsService.getUserPoints(username);

        PointsResponse response = new PointsResponse(
                p.getUser().getUsername(),
                p.getTotalPoints(),
                p.getRedeemedPoints(),
                p.getLastUpdated() != null ? p.getLastUpdated().toString() : null
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{username}/add")
    public ResponseEntity<PointsResponse> addPoints(@PathVariable String username, @RequestParam int points) {
        Points p = rewardsService.addPoints(username, points);

        PointsResponse response = new PointsResponse(
                p.getUser().getUsername(),
                p.getTotalPoints(),
                p.getRedeemedPoints(),
                p.getLastUpdated().toString()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{username}/redeem")
    public ResponseEntity<PointsResponse> redeemPoints(@PathVariable String username, @RequestParam int points) {
        Points p = rewardsService.redeemPoints(username, points);

        PointsResponse response = new PointsResponse(
                p.getUser().getUsername(),
                p.getTotalPoints(),
                p.getRedeemedPoints(),
                p.getLastUpdated().toString()
        );

        return ResponseEntity.ok(response);
    }
}
