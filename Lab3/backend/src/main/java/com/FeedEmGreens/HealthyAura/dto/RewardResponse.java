package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing the result of a successful reward redemption.
 *
 * <p>This class encapsulates details about a user’s reward redemption, including
 * which reward was redeemed, how many points were used, how many remain, and
 * a confirmation message.</p>
 *
 * <p>It is typically returned by the
 * {@link com.FeedEmGreens.HealthyAura.controller.RewardsController} after a user
 * redeems a reward through the <code>/rewards/me/redeem-reward/{rewardId}</code> endpoint.</p>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "username": "johnDoe",
 *   "rewardName": "Free Healthy Drink",
 *   "description": "Redeem a sugar-free drink at participating hawker stalls.",
 *   "pointsUsed": 100,
 *   "pointsLeft": 450,
 *   "message": "Reward redeemed successfully!"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Reward
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 * @see com.FeedEmGreens.HealthyAura.controller.RewardsController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class RewardResponse {

    /** The username of the user who redeemed the reward. */
    private String username;

    /** The name of the redeemed reward. */
    private String rewardName;

    /** A short description of the redeemed reward. */
    private String description;

    /** The number of points used to redeem this reward. */
    private int pointsUsed;

    /** The number of points the user has remaining after redemption. */
    private int pointsLeft;

    /** A success or informational message confirming the redemption status. */
    private String message;

    /**
     * Constructs a new {@code RewardResponse} with full redemption details.
     *
     * @param username    the username of the user
     * @param rewardName  the name of the redeemed reward
     * @param description a short description of the reward
     * @param pointsUsed  the number of points deducted
     * @param pointsLeft  the remaining points after redemption
     * @param message     a confirmation message
     */
    public RewardResponse(String username, String rewardName, String description,
                          int pointsUsed, int pointsLeft, String message) {
        this.username = username;
        this.rewardName = rewardName;
        this.description = description;
        this.pointsUsed = pointsUsed;
        this.pointsLeft = pointsLeft;
        this.message = message;
    }

    /**
     * Returns the username of the user who redeemed the reward.
     *
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Returns the name of the redeemed reward.
     *
     * @return the reward name
     */
    public String getRewardName() { return rewardName; }

    /**
     * Returns a short description of the redeemed reward.
     *
     * @return the reward description
     */
    public String getDescription() { return description; }

    /**
     * Returns the number of points used to redeem the reward.
     *
     * @return the points used
     */
    public int getPointsUsed() { return pointsUsed; }

    /**
     * Returns the number of points left in the user’s account.
     *
     * @return the remaining points
     */
    public int getPointsLeft() { return pointsLeft; }

    /**
     * Returns the confirmation or success message of the redemption.
     *
     * @return the response message
     */
    public String getMessage() { return message; }
}
