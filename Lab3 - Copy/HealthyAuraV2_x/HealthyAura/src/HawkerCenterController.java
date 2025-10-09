package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.service.HawkerCenterApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hawker-centers")
@CrossOrigin(origins = "*")
public class HawkerCenterController {

    private final HawkerCenterApiService hawkerCenterApiService;

    public HawkerCenterController(HawkerCenterApiService hawkerCenterApiService) {
        this.hawkerCenterApiService = hawkerCenterApiService;
    }

    // Sync hawker center data from government API
    @PostMapping("/sync")
    public Map<String, Object> syncHawkerCenters() {
        List<Eatery> syncedEateries = hawkerCenterApiService.fetchAndSyncHawkerCenters();
        return Map.of(
            "message", "Hawker center data synced successfully",
            "count", syncedEateries.size()
        );
    }

    // Get all hawker centers
    @GetMapping
    public List<Eatery> getAllHawkerCenters() {
        return hawkerCenterApiService.getAllHawkerCentersWithTags();
    }

    // Search hawker centers by name
    @GetMapping("/search")
    public List<Eatery> searchHawkerCenters(@RequestParam String name) {
        return hawkerCenterApiService.searchHawkerCentersByName(name);
    }

    // Get hawker centers by area
    @GetMapping("/area/{area}")
    public List<Eatery> getHawkerCentersByArea(@PathVariable String area) {
        return hawkerCenterApiService.getHawkerCentersByArea(area);
    }

    // Update eatery with additional data
    @PutMapping("/{id}/update")
    public Map<String, String> updateEateryData(
            @PathVariable Long id,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Integer priceIndicator,
            @RequestParam(required = false) Integer queueTime,
            @RequestParam(required = false) List<String> additionalTags) {
        
        hawkerCenterApiService.updateEateryWithAdditionalData(id, rating, priceIndicator, queueTime, additionalTags);
        return Map.of("message", "Eatery data updated successfully");
    }

    // Get data statistics
    @GetMapping("/statistics")
    public Map<String, Object> getDataStatistics() {
        return hawkerCenterApiService.getDataStatistics();
    }
}
