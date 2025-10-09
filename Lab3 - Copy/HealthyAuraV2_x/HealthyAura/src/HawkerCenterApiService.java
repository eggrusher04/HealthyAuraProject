package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.entity.DietaryTags;
import com.FeedEmGreens.HealthyAura.repository.EateryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HawkerCenterApiService {

    private final EateryRepository eateryRepository;
    private final RestTemplate restTemplate;
    
    // Singapore Government Data API endpoint for hawker centers
    private static final String HAWKER_CENTERS_API_URL = "https://data.gov.sg/api/action/datastore_search?resource_id=8f6bba57-19fc-4b75-a41f-8c0ba65d7399&limit=1000";
    
    @Autowired
    public HawkerCenterApiService(EateryRepository eateryRepository) {
        this.eateryRepository = eateryRepository;
        this.restTemplate = new RestTemplate();
    }

    // Fetch and sync hawker center data from government API
    public List<Eatery> fetchAndSyncHawkerCenters() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                HAWKER_CENTERS_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) responseBody.get("result");
                List<Map<String, Object>> records = (List<Map<String, Object>>) result.get("records");
                
                List<Eatery> eateries = new ArrayList<>();
                for (Map<String, Object> record : records) {
                    Eatery eatery = mapRecordToEatery(record);
                    if (eatery != null) {
                        eateries.add(eatery);
                    }
                }
                
                // Save to database
                return eateryRepository.saveAll(eateries);
            }
        } catch (Exception e) {
            System.err.println("Error fetching hawker center data: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    // Map API record to Eatery entity
    private Eatery mapRecordToEatery(Map<String, Object> record) {
        try {
            Eatery eatery = new Eatery();
            
            // Map basic fields
            eatery.setName((String) record.get("name"));
            eatery.setBuildingName((String) record.get("building_name"));
            eatery.setBlockNumber((String) record.get("block_number"));
            eatery.setStreetName((String) record.get("street_name"));
            eatery.setDescription((String) record.get("description"));
            eatery.setPhotoUrl((String) record.get("photo_url"));
            eatery.setStatus((String) record.get("status"));
            
            // Map postal code
            Object postalCodeObj = record.get("postal_code");
            if (postalCodeObj != null) {
                if (postalCodeObj instanceof String) {
                    eatery.setPostalCode(Long.parseLong((String) postalCodeObj));
                } else if (postalCodeObj instanceof Number) {
                    eatery.setPostalCode(((Number) postalCodeObj).longValue());
                }
            }
            
            // Map number of cooked food stalls
            Object numStallsObj = record.get("num_cooked_food_stalls");
            if (numStallsObj != null) {
                if (numStallsObj instanceof String) {
                    eatery.setNumCookedFoodStalls(Integer.parseInt((String) numStallsObj));
                } else if (numStallsObj instanceof Number) {
                    eatery.setNumCookedFoodStalls(((Number) numStallsObj).intValue());
                }
            }
            
            // Map coordinates
            Object longitudeObj = record.get("longitude");
            Object latitudeObj = record.get("latitude");
            if (longitudeObj != null && latitudeObj != null) {
                eatery.setLongitude(parseDouble(longitudeObj));
                eatery.setLatitude(parseDouble(latitudeObj));
            }
            
            // Set default values for recommendation fields
            eatery.setAggregateRating(0.0);
            eatery.setPriceIndicator(1);
            eatery.setQueueTime(0);
            eatery.setCategory("Hawker Centre");
            
            // Add default dietary tags based on common hawker center characteristics
            addDefaultDietaryTags(eatery);
            
            return eatery;
            
        } catch (Exception e) {
            System.err.println("Error mapping record to eatery: " + e.getMessage());
            return null;
        }
    }

    // Add default dietary tags to hawker centers
    private void addDefaultDietaryTags(Eatery eatery) {
        List<DietaryTags> tags = new ArrayList<>();
        
        // Add common hawker center tags
        tags.add(new DietaryTags("Budget Friendly", eatery));
        tags.add(new DietaryTags("Local Food", eatery));
        
        // Add tags based on building name or description
        String buildingName = eatery.getBuildingName();
        String description = eatery.getDescription();
        
        if (buildingName != null) {
            String lowerBuildingName = buildingName.toLowerCase();
            if (lowerBuildingName.contains("market") || lowerBuildingName.contains("food centre")) {
                tags.add(new DietaryTags("Traditional", eatery));
            }
        }
        
        if (description != null) {
            String lowerDescription = description.toLowerCase();
            if (lowerDescription.contains("vegetarian") || lowerDescription.contains("vegan")) {
                tags.add(new DietaryTags("Vegetarian", eatery));
            }
            if (lowerDescription.contains("halal")) {
                tags.add(new DietaryTags("Halal", eatery));
            }
            if (lowerDescription.contains("healthy") || lowerDescription.contains("organic")) {
                tags.add(new DietaryTags("Healthy", eatery));
            }
        }
        
        eatery.setDietaryTags(tags);
    }

    // Helper method to parse double values
    private Double parseDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Update eatery with additional data (ratings, queue times, etc.)
    public void updateEateryWithAdditionalData(Long eateryId, Double rating, Integer priceIndicator, 
                                             Integer queueTime, List<String> additionalTags) {
        Optional<Eatery> optionalEatery = eateryRepository.findById(eateryId);
        if (optionalEatery.isPresent()) {
            Eatery eatery = optionalEatery.get();
            
            if (rating != null) {
                eatery.setAggregateRating(rating);
            }
            if (priceIndicator != null) {
                eatery.setPriceIndicator(priceIndicator);
            }
            if (queueTime != null) {
                eatery.setQueueTime(queueTime);
            }
            if (additionalTags != null) {
                for (String tagName : additionalTags) {
                    DietaryTags tag = new DietaryTags(tagName, eatery);
                    eatery.addDietaryTag(tag);
                }
            }
            
            eateryRepository.save(eatery);
        }
    }

    // Get all hawker centers with their tags
    public List<Eatery> getAllHawkerCentersWithTags() {
        return eateryRepository.findAll();
    }

    // Search hawker centers by name
    public List<Eatery> searchHawkerCentersByName(String name) {
        return eateryRepository.findByNameContainingIgnoreCase(name);
    }

    // Get hawker centers by area
    public List<Eatery> getHawkerCentersByArea(String area) {
        return eateryRepository.findByBuildingNameContainingIgnoreCase(area);
    }

    // Sync data periodically (can be called by a scheduled task)
    public void syncData() {
        System.out.println("Starting hawker center data sync...");
        List<Eatery> syncedEateries = fetchAndSyncHawkerCenters();
        System.out.println("Synced " + syncedEateries.size() + " hawker centers");
    }

    // Get statistics about the data
    public Map<String, Object> getDataStatistics() {
        List<Eatery> allEateries = eateryRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEateries", allEateries.size());
        stats.put("eateriesWithCoordinates", allEateries.stream()
                .filter(e -> e.getLatitude() != null && e.getLongitude() != null)
                .count());
        stats.put("eateriesWithTags", allEateries.stream()
                .filter(e -> e.getDietaryTags() != null && !e.getDietaryTags().isEmpty())
                .count());
        
        // Count by area
        Map<String, Long> areaCount = allEateries.stream()
                .filter(e -> e.getBuildingName() != null)
                .collect(Collectors.groupingBy(Eatery::getBuildingName, Collectors.counting()));
        stats.put("areaDistribution", areaCount);
        
        return stats;
    }
}
