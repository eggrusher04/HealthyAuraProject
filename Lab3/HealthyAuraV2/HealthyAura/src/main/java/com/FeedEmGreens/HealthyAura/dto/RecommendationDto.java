package com.FeedEmGreens.HealthyAura.dto;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import java.util.List;
import java.util.ArrayList;

public class RecommendationDto {

    private Long id;
    private String name;
    private String address;
    private String fullAddress;
    private List<String> tags;
    private String description;
    private Double longitude;
    private Double latitude;
    
    // Enhanced recommendation features
    private Double distance; // in km
    private String reason; // one-line reasoning for recommendation
    private Double score; // recommendation score (0-100)


    public RecommendationDto() {}

    public RecommendationDto(Long id, String name, String address, String fullAddress,
                            List<String> tags, String description, Double longitude,
                            Double latitude) {
        this();
        this.id = id;
        this.name = name;
        this.address = address;
        this.fullAddress = fullAddress;
        this.tags = tags;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Enhanced factory method
    public static RecommendationDto fromEatery(Eatery eatery) {
        return fromEatery(eatery, null, null);
    }
    
    public static RecommendationDto fromEatery(Eatery eatery, Double userLat, Double userLng) {
        RecommendationDto dto = new RecommendationDto(
                eatery.getId(),
                eatery.getName(), 
                eatery.getAddress(),
                eatery.getFullAddress(),
                eatery.getTagNames(),
                eatery.getDescription(),
                eatery.getLongitude(),
                eatery.getLatitude()
        );
        
        // Calculate distance if user location provided
        if (userLat != null && userLng != null) {
            dto.distance = calculateDistance(userLat, userLng, eatery.getLatitude(), eatery.getLongitude());
        }
        
        // Generate reason based on distance and tags
        dto.reason = generateReason(dto);
        
        return dto;
    }
    
    // Calculate distance using Haversine formula
    private static Double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return null;
        }
        
        final int R = 6371; // Earth's radius in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    // Generate one-line reason for recommendation
    private static String generateReason(RecommendationDto dto) {
        List<String> reasons = new ArrayList<>();
        
        // Distance-based reasons
        if (dto.distance != null) {
            if (dto.distance < 0.5) {
                reasons.add("Near you");
            } else if (dto.distance < 1.0) {
                reasons.add("Close by");
            } else if (dto.distance < 2.0) {
                reasons.add("Within walking distance");
            }
        }
        
        // Generic tag-based reasons - show first few tags
        List<String> tags = dto.getTags();
        if (!tags.isEmpty()) {
            // Show up to 2 tags as reasons
            int tagCount = Math.min(2, tags.size());
            for (int i = 0; i < tagCount; i++) {
                reasons.add(tags.get(i) + " option");
            }
        }
        
        if (reasons.isEmpty()) {
            return "Recommended for you";
        }
        
        return String.join(" • ", reasons);
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

}
