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
    private String imageUrl;
    /*
    private String area;
    private Double rating;
    private Integer priceIndicator;
    private Integer queueTime;
    private Double distance; // in km
    */
    private String description;
    private Double longitude;
    private Double latitude;
    // do we need indicate recommendation reasons and how;


    public RecommendationDto() {}

    public RecommendationDto(Long id, String name, String address, String fullAddress,  
                             List<String> tags, String imageUrl, String description, Double longitude, 
                             Double latitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.fullAddress = fullAddress;
        //this.area = area;
        this.tags = tags;
        this.imageUrl = imageUrl;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        //this.distance = distance;
    }

    // Factory method
    public static RecommendationDto fromEatery(Eatery eatery) {
        return new RecommendationDto(
                eatery.getId(),
                eatery.getName(),
                eatery.getStreetName(),
                eatery.getFullAddress(),
                //eatery.getArea(),
                eatery.getTagNames(),
                eatery.getPhotoUrl(),
                /* eatery.getAggregateRating(),
                eatery.getPriceIndicator(),
                eatery.getQueueTime(),
                eatery.getCategory(),*/
                eatery.getDescription(),
                eatery.getLongitude(),
                eatery.getLatitude()
                //generateRecommendationReason(eatery),
                //null // distance to be calculated separately using other method?
        );
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

    //public String getArea() { return area; }
    //public void setArea(String area) { this.area = area; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /*public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getPriceIndicator() { return priceIndicator; }
    public void setPriceIndicator(Integer priceIndicator) { this.priceIndicator = priceIndicator; }

    public Integer getQueueTime() { return queueTime; }
    public void setQueueTime(Integer queueTime) { this.queueTime = queueTime; }*/

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    /*public String getRecommendationReason() { return recommendationReason; }
    public void setRecommendationReason(String recommendationReason) { this.recommendationReason = recommendationReason; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    // Helper methods
    public String getPriceRange() {
        if (priceIndicator == null) return "Unknown";
        return switch (priceIndicator) {
            case 1 -> "$";
            case 2 -> "$$";
            case 3 -> "$$$";
            case 4 -> "$$$$";
            case 5 -> "$$$$$";
            default -> "Unknown";
        };
    }

    public String getFormattedRating() {
        if (rating == null) return "No rating";
        return String.format("%.1f", rating);
    }

    public String getFormattedQueueTime() {
        if (queueTime == null || queueTime == 0) return "No wait";
        return queueTime + " min wait";
    }
    
    // method to give recommendation reason?
    private static String generateRecommendationReason(Eatery eatery) {
        List<String> reasons = new ArrayList<>();
        
        if (eatery.getAggregateRating() != null && eatery.getAggregateRating() >= 4.0) {
            reasons.add("Highly rated");
        }
        if (eatery.getPriceIndicator() != null && eatery.getPriceIndicator() <= 2) {
            reasons.add("Budget-friendly");
        }
        if (eatery.getQueueTime() != null && eatery.getQueueTime() <= 10) {
            reasons.add("Quick service");
        }
        if (eatery.getDietaryTags() != null && !eatery.getDietaryTags().isEmpty()) {
            reasons.add("Matches your preferences");
        }
        
        return reasons.isEmpty() ? "Popular choice" : String.join(", ", reasons);
    }*/
}
