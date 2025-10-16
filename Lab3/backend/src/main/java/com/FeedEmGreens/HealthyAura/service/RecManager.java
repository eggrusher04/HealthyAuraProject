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

}
