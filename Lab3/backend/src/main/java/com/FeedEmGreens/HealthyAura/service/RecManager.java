package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.RecommendationDto;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.repository.EateryRepository;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import com.FeedEmGreens.HealthyAura.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating eatery recommendations based on
 * user preferences, distance, dietary tags, and review metrics.
 *
 * <p>This class serves as the recommendation engine for HealthyAura.
 * It integrates multiple factors such as:
 * <ul>
 *   <li>User personalization (dietary preferences)</li>
 *   <li>Geolocation (distance from user)</li>
 *   <li>Eatery metadata (tags, postal code, etc.)</li>
 *   <li>Community review data (health & hygiene scores, popularity)</li>
 * </ul>
 * </p>
 *
 * <p>It supports both <b>general recommendations</b> (for all users)
 * and <b>personalized recommendations</b> (for logged-in users).</p>
 *
 * @see com.FeedEmGreens.HealthyAura.dto.RecommendationDto
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.entity.Users
 * @see com.FeedEmGreens.HealthyAura.repository.EateryRepository
 * @see com.FeedEmGreens.HealthyAura.repository.ReviewRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class RecManager {

    private final EateryRepository eateryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Constructs the recommendation manager with all required repositories.
     *
     * @param eateryRepository repository for retrieving eatery data
     * @param userRepository repository for accessing user profile and preferences
     * @param reviewRepository repository for computing average ratings and review stats
     */
    public RecManager(EateryRepository eateryRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.eateryRepository = eateryRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Generates a general (non-personalized) list of top eateries across the platform.
     *
     * <p>Recommendations are sorted by an internal composite score derived from:
     * <ul>
     *   <li>Number of dietary tags</li>
     *   <li>Review scores (health, hygiene, popularity)</li>
     *   <li>Proximity if available</li>
     * </ul>
     * </p>
     *
     * @return top 5 {@link RecommendationDto} objects ranked by total score
     */
    public List<RecommendationDto> generateRecommendations() {
        List<Eatery> eateries = eateryRepository.findAll();
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .map(dto -> {
                    double score = calculateBasicScore(dto);
                    dto.setScore(score);
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Generates general recommendations while factoring in user location for proximity scoring.
     *
     * @param userLat the user’s current latitude
     * @param userLng the user’s current longitude
     * @return a ranked list of top 5 nearby eateries
     */
    public List<RecommendationDto> generateRecommendations(Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findAll();
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .map(dto -> {
                    double score = calculateBasicScore(dto);
                    dto.setScore(score);
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Generates recommendations filtered by one or more dietary tags.
     *
     * @param tags list of dietary tags to match (e.g. “vegan”, “low-sugar”)
     * @return a list of eateries matching any of the given tags
     */
    public List<RecommendationDto> generateRecommendationsByTags(List<String> tags) {
        List<Eatery> eateries = eateryRepository.findByDietaryTagsIn(tags);
        return eateries.stream().map(RecommendationDto::fromEatery).collect(Collectors.toList());
    }

    /**
     * Generates tag-based recommendations and ranks them by proximity.
     *
     * @param tags list of dietary tags to match
     * @param userLat user’s latitude
     * @param userLng user’s longitude
     * @return a list of nearby eateries filtered by dietary tags
     */
    public List<RecommendationDto> generateRecommendationsByTags(List<String> tags, Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findByDietaryTagsIn(tags);
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .sorted(Comparator.comparing(RecommendationDto::getDistance, Comparator.nullsLast(Double::compareTo)))
                .collect(Collectors.toList());
    }

    /** Generates recommendations for a single tag (no location). */
    public List<RecommendationDto> generateRecommendationsByTag(String tag) {
        List<Eatery> eateries = eateryRepository.findByDietaryTag(tag);
        return eateries.stream().map(RecommendationDto::fromEatery).collect(Collectors.toList());
    }

    /** Generates single-tag recommendations with distance sorting. */
    public List<RecommendationDto> generateRecommendationsByTag(String tag, Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findByDietaryTag(tag);
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .sorted(Comparator.comparing(RecommendationDto::getDistance, Comparator.nullsLast(Double::compareTo)))
                .collect(Collectors.toList());
    }

    /**
     * Generates personalized recommendations based on user preferences and proximity.
     *
     * <p>When user preferences are not set, the algorithm falls back to a
     * <b>cold start strategy</b> that emphasizes distance and general popularity.</p>
     *
     * @param username the current user’s username
     * @param userLat user’s latitude (optional)
     * @param userLng user’s longitude (optional)
     * @return top 5 personalized {@link RecommendationDto} objects ranked by score
     */
    public List<RecommendationDto> generatePersonalizedRecommendations(String username, Double userLat, Double userLng) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Eatery> eateries = eateryRepository.findAll();
        List<String> userPreferences = parseUserPreferences(user.getPreferences());
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (Eatery eatery : eateries) {
            RecommendationDto dto = RecommendationDto.fromEatery(eatery, userLat, userLng);
            double score = userPreferences.isEmpty()
                    ? calculateColdStartScore(dto)
                    : calculatePersonalizedScore(dto, userPreferences, userLat, userLng);
            dto.setScore(score);
            recommendations.add(dto);
        }

        return recommendations.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5)
                .collect(Collectors.toList());
    }

    /** Returns all eateries located at a specific postal code. */
    public List<RecommendationDto> generateRecommendationsByPostalCode(Long postalCode) {
        List<Eatery> eateries = eateryRepository.findByPostalCode(postalCode);
        return eateries.stream().map(RecommendationDto::fromEatery).collect(Collectors.toList());
    }

    /** Returns and ranks eateries by postal code and distance. */
    public List<RecommendationDto> generateRecommendationsByPostalCode(Long postalCode, Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findByPostalCode(postalCode);
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .sorted(Comparator.comparing(RecommendationDto::getDistance, Comparator.nullsLast(Double::compareTo)))
                .collect(Collectors.toList());
    }

    /**
     * Splits a comma-separated user preference string into individual normalized tags.
     *
     * @param preferences the user’s raw preference string
     * @return list of trimmed lowercase preference keywords
     */
    private List<String> parseUserPreferences(String preferences) {
        if (preferences == null || preferences.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(preferences.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Calculates a personalized recommendation score combining:
     * <ul>
     *   <li>Preference matching</li>
     *   <li>Distance proximity</li>
     *   <li>Average review scores and popularity</li>
     * </ul>
     *
     * @param dto eatery recommendation object
     * @param userPreferences user’s list of preferred tags
     * @param userLat latitude of the user (nullable)
     * @param userLng longitude of the user (nullable)
     * @return a bounded score between 0 and 100
     */
    private double calculatePersonalizedScore(RecommendationDto dto, List<String> userPreferences, Double userLat, Double userLng) {
        double score = 10.0;

        // Preference match contribution
        long matches = dto.getTags().stream()
                .filter(tag -> userPreferences.stream().anyMatch(pref -> tag.toLowerCase().contains(pref.toLowerCase())))
                .count();
        score += matches * 20.0;

        // Distance contribution
        if (dto.getDistance() != null) {
            if (dto.getDistance() < 0.5) score += 30.0;
            else if (dto.getDistance() < 1.0) score += 25.0;
            else if (dto.getDistance() < 2.0) score += 20.0;
            else if (dto.getDistance() < 5.0) score += 15.0;
            else score += 10.0;
        }

        // Review contribution
        score += ratingScoreAndPopulate(dto);
        return Math.min(100.0, Math.max(0.0, score));
    }

    /** Calculates a general score based on distance, tags, and ratings. */
    private double calculateBasicScore(RecommendationDto dto) {
        double score = 10.0;

        // Distance-based weight
        if (dto.getDistance() != null) {
            if (dto.getDistance() < 0.5) score += 30.0;
            else if (dto.getDistance() < 1.0) score += 25.0;
            else if (dto.getDistance() < 2.0) score += 20.0;
            else if (dto.getDistance() < 5.0) score += 15.0;
            else score += 10.0;
        }

        // Tag diversity reward
        score += dto.getTags().size() * 5.0;

        // Ratings contribution
        score += ratingScoreAndPopulate(dto);
        return Math.min(100.0, Math.max(0.0, score));
    }

    /** Handles cold-start users (no preferences) using mainly distance and basic metadata. */
    private double calculateColdStartScore(RecommendationDto dto) {
        double score = 10.0;
        if (dto.getDistance() != null) {
            if (dto.getDistance() < 0.5) score += 40.0;
            else if (dto.getDistance() < 1.0) score += 35.0;
            else if (dto.getDistance() < 2.0) score += 30.0;
            else if (dto.getDistance() < 5.0) score += 20.0;
            else score += 10.0;
        } else {
            score += 20.0 + dto.getTags().size() * 8.0;
        }
        return Math.min(100.0, Math.max(0.0, score));
    }

    /**
     * Computes a review-based quality and popularity score for an eatery.
     *
     * <p>The method updates the provided {@link RecommendationDto} with average
     * health and hygiene ratings and total review count.</p>
     *
     * <p>It also returns a score contribution between <b>0 and ~50</b> based on:
     * <ul>
     *   <li>Average review rating (scaled to 0–40)</li>
     *   <li>Popularity (log-scaled review volume)</li>
     * </ul>
     * </p>
     *
     * @param dto the eatery recommendation object to enrich
     * @return the computed rating contribution to the final recommendation score
     */
    private double ratingScoreAndPopulate(RecommendationDto dto) {
        Long eateryId = dto.getId();
        if (eateryId == null) return 0.0;

        Eatery eatery = eateryRepository.findById(eateryId).orElse(null);
        if (eatery == null) return 0.0;

        Double avgHealth = reviewRepository.getAverageHealthScore(eatery);
        Double avgHygiene = reviewRepository.getAverageHygieneScore(eatery);
        Long count = reviewRepository.getReviewCount(eatery);

        dto.setAverageHealth(avgHealth);
        dto.setAverageHygiene(avgHygiene);
        dto.setReviewCount(count);

        double avgScore = 0.0;
        int parts = 0;
        if (avgHealth != null) { avgScore += avgHealth; parts++; }
        if (avgHygiene != null) { avgScore += avgHygiene; parts++; }
        avgScore = parts > 0 ? avgScore / parts : 0.0;

        // Scale quality (0–5) → (0–40)
        double quality = (avgScore / 5.0) * 40.0;

        // Popularity adjustment (logarithmic scale)
        double popularity = 0.0;
        if (count != null && count > 0) {
            double basePopularity = Math.min(10.0, Math.log(count + 1) * 4.0);
            popularity = basePopularity * (avgScore / 5.0);
        }

        return quality + popularity;
    }
}
