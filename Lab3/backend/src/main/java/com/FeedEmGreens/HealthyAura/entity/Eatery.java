package com.FeedEmGreens.HealthyAura.entity;

import java.util.List;
import jakarta.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "healthier_eateries")
public class Eatery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(name = "building_name", length = 255)
    private String buildingName;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "postal_code")
    private Long postalCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    // Relationship with dietary tags
    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DietaryTags> dietaryTags = new ArrayList<>();


    public Eatery() {}

    public Eatery(String name, String buildingName, String blockNumber, String address,
                  Long postalCode, Double longitude, Double latitude) {
        this.name = name;
        this.buildingName = buildingName;
        this.address = address;
        this.postalCode = postalCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Helper methods
    public String getFullAddress() {
        return buildingName + " " + address + ", Singapore " + postalCode;
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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Long getPostalCode() { return postalCode; }
    public void setPostalCode(Long postalCode) { this.postalCode = postalCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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
