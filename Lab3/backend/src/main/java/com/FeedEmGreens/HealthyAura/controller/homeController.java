package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.RecommendationDto;
import com.FeedEmGreens.HealthyAura.service.RecManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller that handles homepage content and personalized or general recommendations for users.
 *
 * <p>This controller powers the landing and recommendation features of the
 * <strong>HealthyAura</strong> application, providing both personalized and general
 * eatery suggestions based on user login status, location, and selected filters.</p>
 *
 * <p>Features include:
 * <ul>
 *     <li>Personalized recommendations for logged-in users</li>
 *     <li>General recommendations for visitors</li>
 *     <li>Filtering recommendations by tag(s) or postal code</li>
 * </ul>
 * </p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/home")
public class homeController {

    /**
     * Service manager responsible for generating personalized and general recommendations.
     */
    private final RecManager recManager;

    /**
     * Constructs a {@code homeController} with a {@link RecManager} dependency.
     *
     * @param recManager the recommendation manager service used to compute eatery recommendations
     */
    public homeController(RecManager recManager) {
        this.recManager = recManager;
    }

    /**
     * Root endpoint for the HealthyAura homepage.
     *
     * <p>Returns a simple greeting or banner message for the landing page.</p>
     *
     * @return a map containing a static message for homepage display
     */
    // Home page endpoint
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "HealthyAura - Recommended for you");
    }

    /**
     * Retrieves a list of recommended eateries for the homepage.
     *
     * <p>If a user is logged in, this returns <strong>personalized recommendations</strong>
     * based on the user’s preferences, dietary profile, and location (if provided).
     * Otherwise, it returns general recommendations optionally filtered by latitude and longitude.</p>
     *
     * @param lat optional latitude for location-based filtering
     * @param lng optional longitude for location-based filtering
     * @return a list of {@link RecommendationDto} objects representing eatery suggestions
     */
    // Get recommendations - personalised if logged in, general if not
    @GetMapping("/recommendations")
    public List<RecommendationDto> getRecommendations(
            @RequestParam(required = false) Double lat, 
            @RequestParam(required = false) Double lng) {
        
        // Check if user is logged in
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username != null && !username.equals("anonymousUser")) {
                // User is logged in - return personalized recommendations
                return recManager.generatePersonalizedRecommendations(username, lat, lng);
            }
        } catch (Exception e) {
            // User not logged in or no authentication context
        }
        
        // User not logged in - return general recommendations
        if (lat != null && lng != null) {
            return recManager.generateRecommendations(lat, lng);
        } else {
            return recManager.generateRecommendations();
        }
    }

    /**
     * Retrieves eatery recommendations filtered by a single tag.
     *
     * <p>This endpoint is used for quick filtering on the homepage (e.g., when a user clicks
     * a category button like <code>Vegetarian</code> or <code>High Protein</code>).</p>
     *
     * @param tag the tag used for filtering recommendations
     * @param lat optional latitude for proximity-based filtering
     * @param lng optional longitude for proximity-based filtering
     * @return a list of {@link RecommendationDto} objects filtered by the given tag
     */
    // Get recommendations by tag (for filter buttons)
    @GetMapping("/recommendations/tag/{tag}")
    public List<RecommendationDto> getRecommendationsByTag(
            @PathVariable String tag,
            @RequestParam(required = false) Double lat, 
            @RequestParam(required = false) Double lng) {
        
        if (lat != null && lng != null) {
            return recManager.generateRecommendationsByTag(tag, lat, lng);
        } else {
            return recManager.generateRecommendationsByTag(tag);
        }
    }

    /**
     * Retrieves eatery recommendations filtered by multiple tags.
     *
     * <p>Useful for more refined filtering, such as showing eateries that match
     * multiple dietary preferences or categories (e.g., <code>Vegan</code> + <code>Low Carb</code>).</p>
     *
     * @param tags a list of tags used for filtering recommendations
     * @param lat  optional latitude for location filtering
     * @param lng  optional longitude for location filtering
     * @return a list of {@link RecommendationDto} objects matching all provided tags
     */
    // Get recommendations by multiple tags
    @GetMapping("/recommendations/tags")
    public List<RecommendationDto> getRecommendationsByTags(
            @RequestParam List<String> tags,
            @RequestParam(required = false) Double lat, 
            @RequestParam(required = false) Double lng) {
        
        if (lat != null && lng != null) {
            return recManager.generateRecommendationsByTags(tags, lat, lng);
        } else {
            return recManager.generateRecommendationsByTags(tags);
        }
    }

    /**
     * Retrieves recommendations based on a postal code area.
     *
     * <p>This endpoint is used for users who prefer searching by location-based identifiers
     * such as postal codes instead of coordinates. The method supports optional latitude
     * and longitude inputs for finer location accuracy.</p>
     *
     * @param postalCode the postal code identifying the target area
     * @param lat        optional latitude for refining search
     * @param lng        optional longitude for refining search
     * @return a list of {@link RecommendationDto} objects within or near the specified postal area
     */
    // Get recommendations by postal code
    @GetMapping("/recommendations/postal/{postalCode}")
    public List<RecommendationDto> getRecommendationsByPostalCode(
            @PathVariable Long postalCode,
            @RequestParam(required = false) Double lat, 
            @RequestParam(required = false) Double lng) {
        
        if (lat != null && lng != null) {
            return recManager.generateRecommendationsByPostalCode(postalCode, lat, lng);
        } else {
            return recManager.generateRecommendationsByPostalCode(postalCode);
        }
    }


}
