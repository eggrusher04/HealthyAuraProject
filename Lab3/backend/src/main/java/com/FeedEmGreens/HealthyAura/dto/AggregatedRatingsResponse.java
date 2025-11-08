package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) representing aggregated review statistics for an eatery.
 *
 * <p>This object summarizes the overall review metrics calculated from user-submitted
 * reviews, typically including:
 * <ul>
 *     <li>Average health rating</li>
 *     <li>Average hygiene rating</li>
 *     <li>Total number of reviews submitted</li>
 * </ul>
 * </p>
 *
 * <p>Both average scores are rounded to one decimal place for display clarity.</p>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "averageHealthScore": 4.5,
 *   "averageHygieneScore": 4.2,
 *   "totalReviews": 18
 * }
 * </pre>
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class AggregatedRatingsResponse {

    /** The average health score across all reviews, rounded to one decimal place. */
    private Double averageHealthScore;

    /** The average hygiene score across all reviews, rounded to one decimal place. */
    private Double averageHygieneScore;

    /** The total number of reviews submitted for the eatery. */
    private Long totalReviews;

    /**
     * Default constructor.
     * <p>Creates an empty {@code AggregatedRatingsResponse} instance.</p>
     */
    public AggregatedRatingsResponse() {}

    /**
     * Constructs an {@code AggregatedRatingsResponse} with the specified values.
     * <p>Average scores are automatically rounded to one decimal place.</p>
     *
     * @param averageHealthScore  the average health score
     * @param averageHygieneScore the average hygiene score
     * @param totalReviews        the total number of reviews
     */
    public AggregatedRatingsResponse(Double averageHealthScore, Double averageHygieneScore, Long totalReviews) {
        this.averageHealthScore = averageHealthScore != null ?
                Math.round(averageHealthScore * 10.0) / 10.0 : null;
        this.averageHygieneScore = averageHygieneScore != null ?
                Math.round(averageHygieneScore * 10.0) / 10.0 : null;
        this.totalReviews = totalReviews;
    }

    /**
     * Returns the average health score.
     *
     * @return the average health score, rounded to one decimal place
     */
    public Double getAverageHealthScore() {
        return averageHealthScore;
    }

    /**
     * Sets the average health score, rounding it to one decimal place.
     *
     * @param averageHealthScore the average health score to set
     */
    public void setAverageHealthScore(Double averageHealthScore) {
        this.averageHealthScore = averageHealthScore != null ?
                Math.round(averageHealthScore * 10.0) / 10.0 : null;
    }

    /**
     * Returns the average hygiene score.
     *
     * @return the average hygiene score, rounded to one decimal place
     */
    public Double getAverageHygieneScore() {
        return averageHygieneScore;
    }

    /**
     * Sets the average hygiene score, rounding it to one decimal place.
     *
     * @param averageHygieneScore the average hygiene score to set
     */
    public void setAverageHygieneScore(Double averageHygieneScore) {
        this.averageHygieneScore = averageHygieneScore != null ?
                Math.round(averageHygieneScore * 10.0) / 10.0 : null;
    }

    /**
     * Returns the total number of reviews used in this aggregation.
     *
     * @return the total review count
     */
    public Long getTotalReviews() {
        return totalReviews;
    }

    /**
     * Sets the total number of reviews used in this aggregation.
     *
     * @param totalReviews the total number of reviews
     */
    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}
