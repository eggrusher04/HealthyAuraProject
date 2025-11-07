package com.FeedEmGreens.HealthyAura.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import com.FeedEmGreens.HealthyAura.entity.Points;
import com.FeedEmGreens.HealthyAura.repository.PointsRepository;
import com.FeedEmGreens.HealthyAura.entity.Reward;
import com.FeedEmGreens.HealthyAura.repository.RewardRepository;
import com.FeedEmGreens.HealthyAura.dto.RewardResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer responsible for handling all reward- and points-related business logic
 * in the HealthyAura ecosystem.
 *
 * <p>This class manages the full lifecycle of the user rewards system:
 * <ul>
 *   <li>Retrieving, adding, and redeeming points</li>
 *   <li>Handling reward redemption and validation</li>
 *   <li>Automatically creating points records for new users</li>
 *   <li>Admin-level point deductions for moderation actions</li>
 * </ul>
 * </p>
 *
 * <p>Access control is enforced via Spring Security. Only the authenticated user can
 * view or modify their own points balance, except in administrative workflows where
 * moderation or system deductions are required.</p>
 *
 * <p>All database operations are managed through Spring Data JPA repositories, with
 * transactional rollbacks enabled on reward redemptions.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.repository.PointsRepository
 * @see com.FeedEmGreens.HealthyAura.repository.RewardRepository
 * @see com.FeedEmGreens.HealthyAura.repository.UserRepository
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.entity.Reward
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class RewardsService {

    private final PointsRepository pointsRepository;
    private final UserRepository userRepository;
    private final RewardRepository rewardRepository;

    /**
     * Constructs a new {@link RewardsService} with required dependencies.
     *
     * @param pointsRepository repository for managing user points
     * @param userRepository repository for accessing user entities
     * @param rewardRepository repository for managing available rewards
     */
    public RewardsService(PointsRepository pointsRepository,
                          UserRepository userRepository,
                          RewardRepository rewardRepository) {
        this.pointsRepository = pointsRepository;
        this.userRepository = userRepository;
        this.rewardRepository = rewardRepository;
    }

    // =====================================================================
    // POINTS MANAGEMENT
    // =====================================================================

    /**
     * Retrieves the {@link Points} record for a specific user, ensuring that only
     * the authenticated user can access their own points balance.
     *
     * <p>If the record does not exist (e.g., a new user), it is automatically created
     * with an initial balance of 0 points.</p>
     *
     * @param username the username of the user whose points are being retrieved
     * @return the {@link Points} entity associated with the user
     * @throws AccessDeniedException if an unauthorized user attempts access
     * @throws RuntimeException if the specified user cannot be found
     */
    public Points getUserPoints(String username) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUser.equals(username)) {
            throw new AccessDeniedException("Access Forbidden.");
        }

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return pointsRepository.findByUser(user)
                .orElseGet(() -> createNewUserPoints(user)); // Auto-create if missing
    }

    /**
     * Adds a specified number of points to a user’s account.
     *
     * <p>This operation updates the {@code totalPoints} field and refreshes
     * the {@code lastUpdated} timestamp.</p>
     *
     * @param username the username of the user
     * @param pointsToAdd number of points to add
     * @return the updated {@link Points} entity
     */
    public Points addPoints(String username, int pointsToAdd) {
        Points points = getUserPoints(username);
        points.setTotalPoints(points.getTotalPoints() + pointsToAdd);
        points.setLastUpdated(LocalDateTime.now());
        return pointsRepository.save(points);
    }

    /**
     * Redeems points for a user, deducting from their total and recording the redeemed amount.
     *
     * <p>If the user has insufficient points, an exception is thrown.</p>
     *
     * @param username the username of the user
     * @param pointsToRedeem number of points to redeem
     * @return the updated {@link Points} entity
     * @throws RuntimeException if insufficient points are available
     */
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

    /**
     * Deducts points from a user account.
     *
     * <p>This method is primarily used by administrators or background operations
     * (e.g., when a user’s review is deleted). It bypasses the authentication check
     * since moderators are allowed to deduct points from any account.</p>
     *
     * <p>Negative balances are permitted according to system policy.</p>
     *
     * @param username the username whose points should be deducted
     * @param pointsToDeduct number of points to remove
     * @return the updated {@link Points} entity (may contain negative balance)
     */
    public Points deductPoints(String username, int pointsToDeduct) {
        Points points = getUserPointsInternal(username);
        points.setTotalPoints(points.getTotalPoints() - pointsToDeduct);
        points.setLastUpdated(LocalDateTime.now());
        return pointsRepository.save(points);
    }

    /**
     * Internal helper that retrieves a user’s points without enforcing authentication checks.
     * Used internally by admin operations.
     *
     * @param username the username
     * @return the associated {@link Points} record (auto-created if missing)
     */
    private Points getUserPointsInternal(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return pointsRepository.findByUser(user)
                .orElseGet(() -> createNewUserPoints(user));
    }

    /**
     * Creates a new {@link Points} record for a given user with default values.
     *
     * @param user the {@link Users} entity
     * @return the newly created {@link Points} entity
     */
    private Points createNewUserPoints(Users user) {
        Points newPoints = new Points();
        newPoints.setUser(user);
        newPoints.setTotalPoints(0);
        newPoints.setRedeemedPoints(0);
        newPoints.setLastUpdated(LocalDateTime.now());
        return pointsRepository.save(newPoints);
    }

    // =====================================================================
    // REWARD MANAGEMENT
    // =====================================================================

    /**
     * Retrieves a list of all available {@link Reward} objects from the system.
     *
     * @return a list of all rewards (active and inactive)
     */
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    /**
     * Adds a new reward to the database (for administrative or demo purposes).
     *
     * @param reward the reward entity to add
     * @return the saved {@link Reward}
     */
    public Reward addReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    /**
     * Redeems a reward for a user, deducting the required number of points and returning
     * a structured response summarizing the transaction.
     *
     * <p>This method is annotated with {@link Transactional} to ensure that if any
     * part of the operation fails (e.g., insufficient points), the entire transaction
     * is rolled back and no partial updates occur.</p>
     *
     * @param username the username of the user redeeming the reward
     * @param rewardId the ID of the reward being redeemed
     * @return a {@link RewardResponse} object containing redemption details
     * @throws RuntimeException if the reward is not found or insufficient points exist
     */
    @Transactional
    public RewardResponse redeemReward(String username, Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        Points updatedPoints = redeemPoints(username, reward.getPointsRequired());

        // Build and return a standardized response DTO
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
