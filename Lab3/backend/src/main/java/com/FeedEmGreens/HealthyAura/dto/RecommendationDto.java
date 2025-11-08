package com.FeedEmGreens.HealthyAura.dto;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object (DTO) representing an eatery recommendation.
 *
 * <p>This DTO aggregates key eatery information, including its location,
 * description, health/hygiene ratings, and dynamically generated recommendation
 * data such as proximity, reason, and overall score.</p>
 *
 * <p>It is primarily used by the recommendation engine in
 * {@link com.FeedEmGreens.HealthyAura.service.RecManager} and served via the
 * {@link com.FeedEmGreens.HealthyAura.controller.homeController} endpoints.</p>
 *
 * <p>Example JSON response:</p>
 * <pre>
 * {
 *   "id": 12,
 *   "name": "GreenBite",
 *   "address": "123 Orchard Road",
 *   "fullAddress": "123 Orchard Road, Singapore 238888",
 *   "tags": ["Vegan", "Budget-Friendly"],
 *   "description": "Healthy and affordable meals",
 *   "longitude": 103.832,
 *   "latitude": 1.303,
 *   "distance": 0.8,
 *   "reason": "Close by • Vegan option",
 *   "score": 92.5,
 *   "averageHealth": 4.6,
 *   "averageHygiene": 4.7,
 *   "reviewCount": 45
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.service.RecManager
 * @see com.FeedEmGreens.HealthyAura.controller.homeController
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class RecommendationDto {

    /** The unique identifier of the eatery. */
    private Long id;

    /** The name of the eatery. */
    private String name;

    /** The short or displayed address of the eatery. */
    private String address;

    /** The complete formatted address of the eatery (including postal code). */
    private String fullAddress;

    /** A list of descriptive tags associated with the eatery (e.g., "Halal", "Low Sugar"). */
    private List<String> tags;

    /** A short textual description of the eatery. */
    private String description;

    /** The eatery’s geographical longitude coordinate. */
    private Double longitude;

    /** The eatery’s geographical latitude coordinate. */
    private Double latitude;

    /** The distance (in kilometers) between the user and the eatery, if available. */
    private Double distance;

    /** A brief one-line reason for why this eatery was recommended. */
    private String reason;

    /** The computed recommendation score (0–100), rounded to one decimal place. */
    private Double score;

    /** The average health rating of the eatery. */
    private Double averageHealth;

    /** The average hygiene rating of the eatery. */
    private Double averageHygiene;

    /** The total number of reviews submitted for the eatery. */
    private Long reviewCount;

    /** Default constructor for framework usage. */
    public RecommendationDto() {}

    /**
     * Constructs a {@code RecommendationDto} with base eatery information.
     *
     * @param id the eatery ID
     * @param name the eatery name
     * @param address the displayed address
     * @param fullAddress the complete formatted address
     * @param tags list of associated tags
     * @param description eatery description
     * @param longitude geographical longitude
     * @param latitude geographical latitude
     */
    public RecommendationDto(Long id, String name, String address, String fullAddress,
                             List<String> tags, String description, Double longitude,
                             Double latitude) {
        this();
        this.id = id;
        this.name = name;
        this.address = address;
        this.fullAddress = fullAddress;
        this.tags = tags;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Creates a {@code RecommendationDto} object from an {@link Eatery} entity.
     *
     * @param eatery the eatery entity
     * @return a new {@code RecommendationDto} representing the eatery
     */
    public static RecommendationDto fromEatery(Eatery eatery) {
        return fromEatery(eatery, null, null);
    }

    /**
     * Creates a {@code RecommendationDto} object from an {@link Eatery} entity,
     * calculating distance and reason if the user's location is available.
     *
     * @param eatery the eatery entity
     * @param userLat user's latitude
     * @param userLng user's longitude
     * @return a new {@code RecommendationDto} with enhanced recommendation data
     */
    public static RecommendationDto fromEatery(Eatery eatery, Double userLat, Double userLng) {
        RecommendationDto dto = new RecommendationDto(
                eatery.getId(),
                eatery.getName(),
                eatery.getAddress(),
                eatery.getFullAddress(),
                eatery.getTagNames(),
                eatery.getDescription(),
                eatery.getLongitude(),
                eatery.getLatitude()
        );

        // Calculate distance if user location provided
        if (userLat != null && userLng != null) {
            dto.distance = calculateDistance(userLat, userLng, eatery.getLatitude(), eatery.getLongitude());
        }

        // Generate a short textual reason
        dto.reason = generateReason(dto);

        return dto;
    }

    /**
     * Calculates the distance between two coordinates using the Haversine formula.
     *
     * @param lat1 latitude of the first point
     * @param lng1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lng2 longitude of the second point
     * @return the distance in kilometers, or {@code null} if inputs are invalid
     */
    private static Double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return null;
        }

        final int R = 6371; // Earth's radius in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Generates a concise, user-friendly reason string based on distance and tags.
     *
     * <p>Examples of generated output:</p>
     * <ul>
     *   <li>“Near you • Vegan option”</li>
     *   <li>“Within walking distance • Halal option”</li>
     *   <li>“Recommended for you” (fallback if no specific reason)</li>
     * </ul>
     *
     * @param dto the {@code RecommendationDto} for which the reason is generated
     * @return a one-line string describing why the eatery was recommended
     */
    private static String generateReason(RecommendationDto dto) {
        List<String> reasons = new ArrayList<>();

        // Distance-based reasons
        if (dto.distance != null) {
            if (dto.distance < 0.5) {
                reasons.add("Near you");
            } else if (dto.distance < 1.0) {
                reasons.add("Close by");
            } else if (dto.distance < 2.0) {
                reasons.add("Within walking distance");
            }
        }

        // Tag-based reasons (up to two)
        List<String> tags = dto.getTags();
        if (!tags.isEmpty()) {
            int tagCount = Math.min(2, tags.size());
            for (int i = 0; i < tagCount; i++) {
                reasons.add(tags.get(i) + " option");
            }
        }

        return reasons.isEmpty() ? "Recommended for you" : String.join(" • ", reasons);
    }

    // --------------------- Getters and Setters ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getScore() { return score; }
    public void setScore(Double score) {
        this.score = (score == null) ? null : Math.round(score * 10.0) / 10.0;
    }

    public Double getAverageHealth() { return averageHealth; }
    public void setAverageHealth(Double averageHealth) { this.averageHealth = averageHealth; }

    public Double getAverageHygiene() { return averageHygiene; }
    public void setAverageHygiene(Double averageHygiene) { this.averageHygiene = averageHygiene; }

    public Long getReviewCount() { return reviewCount; }
    public void setReviewCount(Long reviewCount) { this.reviewCount = reviewCount; }
}
