package com.FeedEmGreens.HealthyAura.entity;

import java.util.List;
import jakarta.persistence.*;
import java.util.ArrayList;

/**
 * Entity representing a healthier eatery registered in the HealthyAura database.
 *
 * <p>Each {@code Eatery} record stores key details about a food establishment,
 * including its location, postal code, description, and associated dietary tags.
 * These entities form the core data source for features like map display,
 * personalized recommendations, and search filters.</p>
 *
 * <p>Entries are stored in the <b>healthier_eateries</b> table.</p>
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>Displaying a list of eateries with their dietary tags on the map UI</li>
 *   <li>Filtering eateries by dietary preferences such as “Vegan” or “Halal”</li>
 *   <li>Computing nearby eateries based on latitude and longitude</li>
 * </ul>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.DietaryTags
 * @see com.FeedEmGreens.HealthyAura.dto.RecommendationDto
 * @see com.FeedEmGreens.HealthyAura.controller.EateryController
 * @see com.FeedEmGreens.HealthyAura.service.EateryService
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "healthier_eateries")
public class Eatery {

    /** Unique identifier for the eatery. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The name of the eatery (e.g., “Green Delights”, “Salad Stop”). */
    @Column(length = 255)
    private String name;

    /** The building name or block where the eatery is located. */
    @Column(name = "building_name", length = 255)
    private String buildingName;

    /** The street address of the eatery. */
    @Column(name = "address", length = 255)
    private String address;

    /** The 6-digit postal code of the eatery. */
    @Column(name = "postal_code")
    private Long postalCode;

    /** A short description or overview of the eatery. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** The longitude coordinate of the eatery’s location. */
    @Column(name = "longitude")
    private Double longitude;

    /** The latitude coordinate of the eatery’s location. */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * The list of dietary tags associated with this eatery.
     * <p>Example: “Vegan”, “Halal”, “Low Sodium”.</p>
     */
    @OneToMany(mappedBy = "eatery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DietaryTags> dietaryTags = new ArrayList<>();

    /** Default constructor for JPA. */
    public Eatery() {}

    /**
     * Constructs an {@code Eatery} instance with specified details.
     *
     * @param name          the name of the eatery
     * @param buildingName  the building name or block
     * @param blockNumber   unused placeholder for potential address expansion
     * @param address       the street address
     * @param postalCode    the postal code
     * @param longitude     the longitude coordinate
     * @param latitude      the latitude coordinate
     */
    public Eatery(String name, String buildingName, String blockNumber, String address,
                  Long postalCode, Double longitude, Double latitude) {
        this.name = name;
        this.buildingName = buildingName;
        this.address = address;
        this.postalCode = postalCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Returns the formatted full address string for display purposes.
     *
     * @return the full address in the format “BuildingName Address, Singapore PostalCode”
     */
    public String getFullAddress() {
        return buildingName + " " + address + ", Singapore " + postalCode;
    }

    /**
     * Retrieves the names of all dietary tags associated with this eatery.
     *
     * @return a list of tag names (e.g., “Vegan”, “Halal”)
     */
    public List<String> getTagNames() {
        return dietaryTags.stream()
                .map(DietaryTags::getTag)
                .toList();
    }

    /** @return the unique identifier of the eatery */
    public Long getId() { return id; }

    /** @param id sets the unique identifier of the eatery */
    public void setId(Long id) { this.id = id; }

    /** @return the name of the eatery */
    public String getName() { return name; }

    /** @param name sets the name of the eatery */
    public void setName(String name) { this.name = name; }

    /** @return the building name of the eatery */
    public String getBuildingName() { return buildingName; }

    /** @param buildingName sets the building name */
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    /** @return the street address */
    public String getAddress() { return address; }

    /** @param address sets the street address */
    public void setAddress(String address) { this.address = address; }

    /** @return the postal code */
    public Long getPostalCode() { return postalCode; }

    /** @param postalCode sets the postal code */
    public void setPostalCode(Long postalCode) { this.postalCode = postalCode; }

    /** @return the description of the eatery */
    public String getDescription() { return description; }

    /** @param description sets the description */
    public void setDescription(String description) { this.description = description; }

    /** @return the longitude coordinate */
    public Double getLongitude() { return longitude; }

    /** @param longitude sets the longitude coordinate */
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    /** @return the latitude coordinate */
    public Double getLatitude() { return latitude; }

    /** @param latitude sets the latitude coordinate */
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    /** @return the list of dietary tags associated with this eatery */
    public List<DietaryTags> getDietaryTags() { return dietaryTags; }

    /** @param dietaryTags sets the list of dietary tags for this eatery */
    public void setDietaryTags(List<DietaryTags> dietaryTags) { this.dietaryTags = dietaryTags; }

    /**
     * Adds a new dietary tag to the eatery and updates the bidirectional relationship.
     *
     * @param tag the {@link DietaryTags} entity to associate with this eatery
     */
    public void addDietaryTag(DietaryTags tag) {
        dietaryTags.add(tag);
        if (tag != null) {
            tag.setEatery(this);
        }
    }

    /**
     * Removes a dietary tag from the eatery and clears its relationship.
     *
     * @param tag the {@link DietaryTags} entity to remove
     */
    public void removeDietaryTag(DietaryTags tag) {
        dietaryTags.remove(tag);
        if (tag != null) {
            tag.setEatery(null);
        }
    }
}
