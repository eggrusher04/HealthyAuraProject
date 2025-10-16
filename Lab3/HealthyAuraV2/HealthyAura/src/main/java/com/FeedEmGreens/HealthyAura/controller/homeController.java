package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.RecommendationDto;
import com.FeedEmGreens.HealthyAura.service.RecManager;
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

    // Get general recommendations
    @GetMapping("/recommendations")
    public List<RecommendationDto> getRecommendations() {
        return recManager.generateRecommendations();
    }

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


}
