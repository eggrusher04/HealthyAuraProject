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

    @Column(name = "num_cooked_food_stalls")
    private Integer numCookedFoodStalls;

    private Double longitude;
    private Double latitude;

    // Relationship with dietary tags
    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DietaryTags> dietaryTags = new ArrayList<>();


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

    public Integer getNumCookedFoodStalls() { return numCookedFoodStalls; }
    public void setNumCookedFoodStalls(Integer numCookedFoodStalls) { this.numCookedFoodStalls = numCookedFoodStalls; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

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
