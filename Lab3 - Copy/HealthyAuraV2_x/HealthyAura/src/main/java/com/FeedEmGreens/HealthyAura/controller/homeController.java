package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.RecommendationDto;
import com.FeedEmGreens.HealthyAura.service.RecManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")

public class homeController {

    private final RecManager recManager;

    public homeController(RecManager recManager) {
        this.recManager = recManager;
    }

    // Home page endpoint
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "HealthyAura - Recommended for you");
    }

    // Get general recommendations
    @GetMapping("/recommendations")
    public List<RecommendationDto> getRecommendations() {
        return recManager.generateRecommendations();
    }

    /*// Get personalized recommendations
    @GetMapping("/recommendations/personalized")
    public List<RecommendationDto> getPersonalizedRecommendations(
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer maxQueueTime,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "5.0") Double radius) {
        
        return recManager.generatePersonalizedRecommendations(
                tags, minRating, maxPrice, maxQueueTime, latitude, longitude, radius);
    }*/

    // Get recommendations by tag (for filter buttons)
    @GetMapping("/recommendations/tag/{tag}")
    public List<RecommendationDto> getRecommendationsByTag(@PathVariable String tag) {
        return recManager.generateRecommendationsByTag(tag);
    }

    // Get recommendations by multiple tags
    @GetMapping("/recommendations/tags")
    public List<RecommendationDto> getRecommendationsByTags(@RequestParam List<String> tags) {
        return recManager.generateRecommendationsByTags(tags);
    }

    /*// Get budget-friendly recommendations
    @GetMapping("/recommendations/budget")
    public List<RecommendationDto> getBudgetFriendlyRecommendations() {
        return recManager.generateBudgetFriendlyRecommendations();
    }

    // Get recommendations by area
    @GetMapping("/recommendations/area/{area}")
    public List<RecommendationDto> getRecommendationsByArea(@PathVariable String area) {
        return recManager.generateRecommendationsByArea(area);
    }

    // Get recommendations by postal code
    @GetMapping("/recommendations/postal/{postalCode}")
    public List<RecommendationDto> getRecommendationsByPostalCode(@PathVariable Long postalCode) {
        return recManager.generateRecommendationsByPostalCode(postalCode);
    }

    // Get filtered recommendations
    @GetMapping("/recommendations/filtered")
    public List<RecommendationDto> getFilteredRecommendations(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer maxQueueTime,
            @RequestParam(required = false) String area) {
        
        return recManager.generateFilteredRecommendations(tag, minRating, maxPrice, maxQueueTime, area);
    }

    // Get popular tags for filter buttons
    @GetMapping("/tags/popular")
    public List<String> getPopularTags() {
        return recManager.getPopularTags();
    }

    // Get available areas
    @GetMapping("/areas")
    public List<String> getAvailableAreas() {
        return recManager.getAvailableAreas();
    }

    // Get recommendations with distance calculation
    @GetMapping("/recommendations/nearby")
    public List<RecommendationDto> getNearbyRecommendations(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "5.0") Double radius,
            @RequestParam(required = false) List<String> tags) {
        
        List<RecommendationDto> recommendations;
        
        if (tags != null && !tags.isEmpty()) {
            recommendations = recManager.generateRecommendationsByTags(tags);
        } else {
            recommendations = recManager.generateRecommendations();
        }
        
        return recManager.addDistanceToRecommendations(recommendations, latitude, longitude);
    }*/

}
