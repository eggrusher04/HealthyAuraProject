package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.RecommendationDto;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.repository.EateryRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecManager {
    
    private final EateryRepository eateryRepository;

    public RecManager(EateryRepository eateryRepository) {
        this.eateryRepository = eateryRepository;
    }

    // Generate general recommendations
    public List<RecommendationDto> generateRecommendations() {
        List<Eatery> eateries = eateryRepository.findAll();
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    /*
    // Generate personalized recommendations based on user preferences
    public List<RecommendationDto> generatePersonalizedRecommendations(
            List<String> preferredTags,
            Double minRating,
            Integer maxPrice,
            Integer maxQueueTime,
            Double latitude,
            Double longitude,
            Double radius) {
        
        List<Eatery> eateries;
        
        if (latitude != null && longitude != null && radius != null) {
            // Location-based recommendations
            if (preferredTags != null && !preferredTags.isEmpty()) {
                eateries = eateryRepository.findByTagsAndLocation(preferredTags, latitude, longitude, radius);
            } else {
                eateries = eateryRepository.findNearbyEateries(latitude, longitude, radius);
            }
        } else {
            // General personalized recommendations
            eateries = eateryRepository.findPersonalizedRecommendations(
                    preferredTags, minRating, maxPrice, maxQueueTime);
        }
        
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }*/

    // Generate recommendations by dietary tags
    public List<RecommendationDto> generateRecommendationsByTags(List<String> tags) {
        List<Eatery> eateries = eateryRepository.findByDietaryTagsIn(tags);
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    // Generate recommendations by single tag
    public List<RecommendationDto> generateRecommendationsByTag(String tag) {
        List<Eatery> eateries = eateryRepository.findByDietaryTag(tag);
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    /* // Generate budget-friendly recommendations
    public List<RecommendationDto> generateBudgetFriendlyRecommendations() {
        List<Eatery> eateries = eateryRepository.findBudgetFriendlyEateries();
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }*/

    // Generate recommendations by postal code
    public List<RecommendationDto> generateRecommendationsByPostalCode(Long postalCode) {
        List<Eatery> eateries = eateryRepository.findByPostalCode(postalCode);
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    /* // Generate recommendations with filtering
    public List<RecommendationDto> generateFilteredRecommendations(
            String tag,
            Double minRating,
            Integer maxPrice,
            Integer maxQueueTime,
            String area) {
        
        List<Eatery> eateries = new ArrayList<>();
        
        // Start with all eateries
        List<Eatery> allEateries = eateryRepository.findAll();
        
        // Apply filters
        if (tag != null && !tag.isEmpty()) {
            eateries = eateryRepository.findByDietaryTag(tag);
        } else {
            eateries = allEateries;
        }
        
        // Apply additional filters
        if (minRating != null) {
            eateries = eateries.stream()
                    .filter(e -> e.getAggregateRating() != null && e.getAggregateRating() >= minRating)
                    .collect(Collectors.toList());
        }
        
        if (maxPrice != null) {
            eateries = eateries.stream()
                    .filter(e -> e.getPriceIndicator() != null && e.getPriceIndicator() <= maxPrice)
                    .collect(Collectors.toList());
        }
        
        if (maxQueueTime != null) {
            eateries = eateries.stream()
                    .filter(e -> e.getQueueTime() != null && e.getQueueTime() <= maxQueueTime)
                    .collect(Collectors.toList());
        }
        
        if (area != null && !area.isEmpty()) {
            eateries = eateries.stream()
                    .filter(e -> e.getBuildingName() != null && 
                               e.getBuildingName().toLowerCase().contains(area.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    // Get popular tags for filter buttons
    public List<String> getPopularTags() {
        return Arrays.asList("Vegan", "Healthy", "Vegetarian", "High Protein", "Budget Friendly", 
                           "Halal", "Low Carb", "Gluten Free", "Organic", "Quick Service");
    }

    // Get available areas for filtering
    public List<String> getAvailableAreas() {
        return eateryRepository.findAll().stream()
                .map(Eatery::getBuildingName)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Calculate distance between two points (Haversine formula)
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Add distance information to recommendations
    public List<RecommendationDto> addDistanceToRecommendations(List<RecommendationDto> recommendations, 
                                                              Double userLatitude, Double userLongitude) {
        if (userLatitude == null || userLongitude == null) {
            return recommendations;
        }
        
        return recommendations.stream()
                .peek(rec -> {
                    if (rec.getLatitude() != null && rec.getLongitude() != null) {
                        double distance = calculateDistance(userLatitude, userLongitude, 
                                                          rec.getLatitude(), rec.getLongitude());
                        rec.setDistance(distance);
                    }
                })
                .sorted(Comparator.comparing(rec -> rec.getDistance() != null ? rec.getDistance() : Double.MAX_VALUE))
                .collect(Collectors.toList());
    }*/
}
