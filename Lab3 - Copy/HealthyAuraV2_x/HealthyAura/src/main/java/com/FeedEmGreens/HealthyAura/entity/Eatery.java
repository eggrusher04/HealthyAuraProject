package com.FeedEmGreens.HealthyAura.entity;

import java.util.List;
import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "hawker_centres")
public class Eatery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(name = "building_name", length = 255)
    private String buildingName;

    @Column(name = "block_number", length = 50)
    private String blockNumber;

    @Column(name = "street_name", length = 255)
    private String streetName;

    @Column(name = "postal_code")
    private Long postalCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(length = 100)
    private String status;

    @Column(name = "num_cooked_food_stalls")
    private Integer numCookedFoodStalls;

    private Double longitude;
    private Double latitude;

    /*other fields for recommendations? 
    @Column(name = "aggregate_rating")
    private Double aggregateRating = 0.0;

    @Column(name = "price_indicator")
    private Integer priceIndicator = 1; // 1-5 scale

    @Column(name = "queue_time")
    private Integer queueTime = 0; // in minutes
    */
    // Relationship with dietary tags
    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DietaryTags> dietaryTags = new ArrayList<>();

    // Constructors
    public Eatery() {}

    public Eatery(String name, String buildingName, String blockNumber, String streetName, 
                  Long postalCode, Double longitude, Double latitude) {
        this.name = name;
        this.buildingName = buildingName;
        this.blockNumber = blockNumber;
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Helper methods
    public String getFullAddress() {
        return blockNumber + " " + streetName + ", Singapore " + postalCode;
    }

    public String getArea() {
        return buildingName != null ? buildingName : "Unknown Area";
    }

    public List<String> getTagNames() {
        return dietaryTags.stream()
                .map(tag -> tag.getTag())
                .toList();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getBlockNumber() { return blockNumber; }
    public void setBlockNumber(String blockNumber) { this.blockNumber = blockNumber; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public Long getPostalCode() { return postalCode; }
    public void setPostalCode(Long postalCode) { this.postalCode = postalCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    /*public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    */
    public Integer getNumCookedFoodStalls() { return numCookedFoodStalls; }
    public void setNumCookedFoodStalls(Integer numCookedFoodStalls) { this.numCookedFoodStalls = numCookedFoodStalls; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    /*public Double getAggregateRating() { return aggregateRating; }
    public void setAggregateRating(Double aggregateRating) { this.aggregateRating = aggregateRating; }

    public Integer getPriceIndicator() { return priceIndicator; }
    public void setPriceIndicator(Integer priceIndicator) { this.priceIndicator = priceIndicator; }

    public Integer getQueueTime() { return queueTime; }
    public void setQueueTime(Integer queueTime) { this.queueTime = queueTime; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }*/

    public List<DietaryTags> getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(List<DietaryTags> dietaryTags) { this.dietaryTags = dietaryTags; }

    public void addDietaryTag(DietaryTags tag) {
        dietaryTags.add(tag);
        if (tag != null) {
            tag.setEatery(this);
        }
    }

    public void removeDietaryTag(DietaryTags tag) {
        dietaryTags.remove(tag);
        if (tag != null) {
            tag.setEatery(null);
        }
    }
}
