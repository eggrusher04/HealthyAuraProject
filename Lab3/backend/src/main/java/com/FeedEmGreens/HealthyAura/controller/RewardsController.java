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

/**
 * Controller responsible for managing user reward and points systems.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *     <li>Viewing and managing user reward points</li>
 *     <li>Redeeming points or specific rewards</li>
 *     <li>Accessing the available reward catalog</li>
 * </ul>
 *
 * <p>All user-specific endpoints derive the username from the current security context
 * ({@link SecurityContextHolder}) to ensure that only authenticated users can access their data.</p>
 *
 * <p>Administrative or demo endpoints (e.g., adding rewards) are also provided
 * for seeding and testing purposes.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/rewards")
public class RewardsController {

    /**
     * Service layer responsible for handling reward catalog and user points operations.
     */
    private final RewardsService rewardsService;

    /**
     * Constructs a {@code RewardsController} with a {@link RewardsService} dependency.
     *
     * @param rewardsService the service managing rewards and user points
     */
    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    // ---------------- POINTS ENDPOINTS ----------------

    /**
     * Retrieves the authenticated user’s current reward points.
     *
     * <p>Endpoint: <code>GET /rewards/me</code></p>
     *
     * @return a {@link ResponseEntity} containing the user’s {@link PointsResponse},
     *         including total, redeemed, and last updated points
     */
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

    /**
     * Adds points to the authenticated user's account.
     *
     * <p>This may be triggered by certain user activities such as
     * submitting reviews, completing challenges, or other gamified actions.</p>
     *
     * <p>Endpoint: <code>POST /rewards/me/add?points=XX</code></p>
     *
     * @param points the number of points to be added
     * @return a {@link ResponseEntity} containing the updated {@link PointsResponse}
     */
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

    /**
     * Redeems a specified number of points without claiming a specific reward.
     *
     * <p>This is a general redemption endpoint where points are deducted directly
     * (e.g., for in-app actions or discounts not tied to the reward catalog).</p>
     *
     * <p>Endpoint: <code>POST /rewards/me/redeem?points=XX</code></p>
     *
     * @param points the number of points to redeem
     * @return a {@link ResponseEntity} containing the updated {@link PointsResponse}
     */
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

    /**
     * Retrieves a list of all available rewards in the catalog.
     *
     * <p>Used for display on the rewards redemption page, showing
     * available rewards along with their point costs and descriptions.</p>
     *
     * <p>Endpoint: <code>GET /rewards/catalog</code></p>
     *
     * @return a {@link ResponseEntity} containing a list of {@link Reward} entities
     */
    @GetMapping("/catalog")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardsService.getAllRewards());
    }


    /**
     * Adds a new reward to the catalog.
     *
     * <p>This endpoint can be used by admins or during development/demo
     * for seeding new rewards into the system.</p>
     *
     * <p>Endpoint: <code>POST /rewards/catalog/add</code></p>
     *
     * @param reward the {@link Reward} entity to add to the catalog
     * @return a {@link ResponseEntity} containing the newly added {@link Reward}
     */
    @PostMapping("/catalog/add")
    public ResponseEntity<Reward> addReward(@RequestBody Reward reward) {
        return ResponseEntity.ok(rewardsService.addReward(reward));
    }

    /**
     * Redeems a specific reward for the authenticated user.
     *
     * <p>This endpoint deducts the corresponding number of points
     * and marks the reward as redeemed by the user.</p>
     *
     * <p>Endpoint: <code>POST /rewards/me/redeem-reward/{rewardId}</code></p>
     *
     * @param rewardId the ID of the reward being redeemed
     * @return a {@link ResponseEntity} containing a {@link RewardResponse}
     *         summarizing the redeemed reward and updated points balance
     */
    @PostMapping("/me/redeem-reward/{rewardId}")
    public ResponseEntity<RewardResponse> redeemReward(
            @PathVariable Long rewardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        RewardResponse response = rewardsService.redeemReward(username, rewardId);
        return ResponseEntity.ok(response);
    }


}
