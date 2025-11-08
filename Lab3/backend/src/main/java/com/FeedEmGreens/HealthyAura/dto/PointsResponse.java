package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing a user's reward points summary.
 *
 * <p>This class encapsulates information about a user's total points,
 * redeemed points, and the last time their points record was updated.
 * It is primarily used as a response object for endpoints in the
 * {@link com.FeedEmGreens.HealthyAura.controller.RewardsController}.</p>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "username": "johnDoe",
 *   "totalPoints": 1200,
 *   "redeemedPoints": 300,
 *   "lastUpdated": "2025-11-07T14:32:15"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Points
 * @see com.FeedEmGreens.HealthyAura.service.RewardsService
 * @see com.FeedEmGreens.HealthyAura.controller.RewardsController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class PointsResponse {

    /** The username of the user associated with this points record. */
    private String username;

    /** The total number of points the user has accumulated. */
    private int totalPoints;

    /** The number of points the user has redeemed so far. */
    private int redeemedPoints;

    /** The timestamp (in ISO format) when the points were last updated. */
    private String lastUpdated;

    /**
     * Constructs a {@code PointsResponse} with the specified user details and point values.
     *
     * @param username       the username associated with the points record
     * @param totalPoints    the total points the user currently holds
     * @param redeemedPoints the number of points already redeemed
     * @param lastUpdated    the timestamp when the record was last modified
     */
    public PointsResponse(String username, int totalPoints, int redeemedPoints, String lastUpdated) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.redeemedPoints = redeemedPoints;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns the username associated with this points response.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the total number of points accumulated by the user.
     *
     * @return the total points value
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Returns the number of points the user has redeemed.
     *
     * @return the redeemed points count
     */
    public int getRedeemedPoints() {
        return redeemedPoints;
    }

    /**
     * Returns the timestamp when the user's points were last updated.
     *
     * @return the last updated timestamp as a string
     */
    public String getLastUpdated() {
        return lastUpdated;
    }
}
