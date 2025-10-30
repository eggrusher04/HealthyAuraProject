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

@Service
public class RecManager {
    
    private final EateryRepository eateryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public RecManager(EateryRepository eateryRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.eateryRepository = eateryRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    // Generate general recommendations (sorted by score, top 5)
    public List<RecommendationDto> generateRecommendations() {
        List<Eatery> eateries = eateryRepository.findAll();
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .map(dto -> {
                    // Calculate basic score based on tags and distance
                    double score = calculateBasicScore(dto);
                    dto.setScore(score);
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5) // Return top 5 recommendations
                .collect(Collectors.toList());
    }

    // Generate recommendations with user location (sorted by score, top 5)
    public List<RecommendationDto> generateRecommendations(Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findAll();
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .map(dto -> {
                    // Calculate score based on distance and tags
                    double score = calculateBasicScore(dto);
                    dto.setScore(score);
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5) // Return top 5 recommendations
                .collect(Collectors.toList());
    }

    // Generate recommendations by dietary tags
    public List<RecommendationDto> generateRecommendationsByTags(List<String> tags) {
        List<Eatery> eateries = eateryRepository.findByDietaryTagsIn(tags);
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    // Generate recommendations by dietary tags with user location
    public List<RecommendationDto> generateRecommendationsByTags(List<String> tags, Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findByDietaryTagsIn(tags);
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .sorted((a, b) -> {
                    if (a.getDistance() != null && b.getDistance() != null) {
                        return Double.compare(a.getDistance(), b.getDistance());
                    }
                    return a.getName().compareTo(b.getName());
                })
                .collect(Collectors.toList());
    }

    // Generate recommendations by single tag
    public List<RecommendationDto> generateRecommendationsByTag(String tag) {
        List<Eatery> eateries = eateryRepository.findByDietaryTag(tag);
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    // Generate recommendations by single tag with user location
    public List<RecommendationDto> generateRecommendationsByTag(String tag, Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findByDietaryTag(tag);
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .sorted((a, b) -> {
                    if (a.getDistance() != null && b.getDistance() != null) {
                        return Double.compare(a.getDistance(), b.getDistance());
                    }
                    return a.getName().compareTo(b.getName());
                })
                .collect(Collectors.toList());
    }

    // Generate personalized recommendations based on user preferences
    public List<RecommendationDto> generatePersonalizedRecommendations(String username, Double userLat, Double userLng) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        List<Eatery> eateries = eateryRepository.findAll();
        List<RecommendationDto> recommendations = new ArrayList<>();
        
        // Check if user has preferences set (cold start detection)
        List<String> userPreferences = parseUserPreferences(user.getPreferences());
        
        for (Eatery eatery : eateries) {
            RecommendationDto dto = RecommendationDto.fromEatery(eatery, userLat, userLng);
            
            // Calculate score based on whether user has preferences
            double score;
            if (userPreferences.isEmpty()) {
                // Cold start - score mainly based on distance
                score = calculateColdStartScore(dto);
            } else {
                // Personalized - score based on preferences and distance
                score = calculatePersonalizedScore(dto, userPreferences, userLat, userLng);
            }
            dto.setScore(score);
            recommendations.add(dto);
        }
        
        // Sort by score (highest first) and return top recommendations
        return recommendations.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(5) // Return top 5 recommendations
                .collect(Collectors.toList());
    }

    // Generate recommendations by postal code
    public List<RecommendationDto> generateRecommendationsByPostalCode(Long postalCode) {
        List<Eatery> eateries = eateryRepository.findByPostalCode(postalCode);
        return eateries.stream()
                .map(RecommendationDto::fromEatery)
                .collect(Collectors.toList());
    }

    // Generate recommendations by postal code with user location
    public List<RecommendationDto> generateRecommendationsByPostalCode(Long postalCode, Double userLat, Double userLng) {
        List<Eatery> eateries = eateryRepository.findByPostalCode(postalCode);
        return eateries.stream()
                .map(eatery -> RecommendationDto.fromEatery(eatery, userLat, userLng))
                .sorted((a, b) -> {
                    if (a.getDistance() != null && b.getDistance() != null) {
                        return Double.compare(a.getDistance(), b.getDistance());
                    }
                    return a.getName().compareTo(b.getName());
                })
                .collect(Collectors.toList());
    }

    // Parse user preferences string into list of tags
    private List<String> parseUserPreferences(String preferences) {
        if (preferences == null || preferences.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(preferences.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // Calculate personalized score based on user preferences and location
    private double calculatePersonalizedScore(RecommendationDto dto, List<String> userPreferences, Double userLat, Double userLng) {
        double score = 0.0;
        
        // Base score for any eatery
        score += 10.0;
        
        // Score for dietary preferences match (50% of total score)
        int matches = 0;
        for (String preference : userPreferences) {
            if (dto.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(preference.toLowerCase()))) {
                matches++;
            }
        }
        if (matches > 0) {
            score += (matches * 20.0); // 20 points per matching preference
        }
        
        // Score for distance (40% of total score)
        if (dto.getDistance() != null) {
            if (dto.getDistance() < 0.5) {
                score += 30.0; // Very close
            } else if (dto.getDistance() < 1.0) {
                score += 25.0; // Close
            } else if (dto.getDistance() < 2.0) {
                score += 20.0; // Reasonable distance
            } else if (dto.getDistance() < 5.0) {
                score += 15.0; // Far but accessible
            } else {
                score += 10.0; // Very far
            }
        }
        
        // Ratings contribution (and populate dto ratings fields)
        score += ratingScoreAndPopulate(dto);

        // Ensure score is between 0 and 100
        return Math.min(100.0, Math.max(0.0, score));
    }

    // Calculate basic score for general recommendations
    private double calculateBasicScore(RecommendationDto dto) {
        double score = 10.0; // Base score
        
        // Score based on distance
        if (dto.getDistance() != null) {
            if (dto.getDistance() < 0.5) {
                score += 30.0; // Very close
            } else if (dto.getDistance() < 1.0) {
                score += 25.0; // Close
            } else if (dto.getDistance() < 2.0) {
                score += 20.0; // Reasonable distance
            } else if (dto.getDistance() < 5.0) {
                score += 15.0; // Far but accessible
            } else {
                score += 10.0; // Very far
            }
        }
        
        // Score based on number of tags (more tags = more options)
        score += dto.getTags().size() * 5.0;

        // Ratings contribution (and populate dto ratings fields)
        score += ratingScoreAndPopulate(dto);

        return Math.min(100.0, Math.max(0.0, score));
    }

    // Calculate cold start score (mainly distance-based)
    private double calculateColdStartScore(RecommendationDto dto) {
        double score = 10.0; // Base score
        
        // Score mainly based on distance for cold start
        if (dto.getDistance() != null) {
            if (dto.getDistance() < 0.5) {
                score += 40.0; // Very close
            } else if (dto.getDistance() < 1.0) {
                score += 35.0; // Close
            } else if (dto.getDistance() < 2.0) {
                score += 30.0; // Reasonable distance
            } else if (dto.getDistance() < 5.0) {
                score += 20.0; // Far but accessible
            } else {
                score += 10.0; // Very far
            }
        } else {
            // No location provided - score based on number of tags and general appeal
            score += 20.0; // Base score for no location
            score += dto.getTags().size() * 8.0; // More tags = more options
        }
        
        return Math.min(100.0, Math.max(0.0, score));
    }

    // Compute a bounded score contribution from reviews (averages and volume) and set dto fields
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

        double avg = 0.0;
        int parts = 0;
        if (avgHealth != null) { avg += avgHealth; parts++; }
        if (avgHygiene != null) { avg += avgHygiene; parts++; }
        double avgScore = parts > 0 ? (avg / parts) : 0.0; // 0..5

        // Map average 0..5 → 0..40
        double quality = (avgScore / 5.0) * 40.0;

        // Popularity bonus based on volume, bounded ~0..10 and scaled by quality 
        //(penalise low-rated eateries by using avgScore/5.0)
        double popularity = 0.0;
        if (count != null && count > 0) {
            double basePopularity = Math.min(10.0, Math.log(count + 1) * 4.0);
            popularity = basePopularity * (avgScore / 5.0);
        }

        return quality + popularity;
    }
}