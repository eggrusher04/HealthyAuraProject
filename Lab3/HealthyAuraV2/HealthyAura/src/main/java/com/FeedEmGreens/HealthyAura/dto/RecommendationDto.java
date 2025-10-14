package com.FeedEmGreens.HealthyAura.dto;

import com.FeedEmGreens.HealthyAura.entity.Eatery;
import java.util.List;

public class RecommendationDto {

    private Long id;
    private String name;
    private String address;
    private String fullAddress;
    private List<String> tags;
    private String description;
    private Double longitude;
    private Double latitude;
    // do we need indicate recommendation reasons;


    public RecommendationDto() {}

    public RecommendationDto(Long id, String name, String address, String fullAddress
                            List<String> tags, String description, Double longitude,
                            Double latitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.fullAddress = fullAddress;
        this.tags = tags;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Factory method
    public static RecommendationDto fromEatery(Eatery eatery) {
        return new RecommendationDto(
                eatery.getId(),
                eatery.getName(), 
                eatery.getAddress(),
                eatery.getFullAddress(),
                eatery.getTagNames(),
                eatery.getDescription(),
                eatery.getLongitude(),
                eatery.getLatitude()
                //generateRecommendationReason(eatery),
                //distance to be calculated separately using other method?
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

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

}
