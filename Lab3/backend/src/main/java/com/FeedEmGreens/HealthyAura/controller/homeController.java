package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.RecommendationDto;
import com.FeedEmGreens.HealthyAura.service.RecManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
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
