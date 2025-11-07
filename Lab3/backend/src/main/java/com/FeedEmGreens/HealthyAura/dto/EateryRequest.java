package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) used for handling eatery creation, update,
 * and retrieval requests between the backend and frontend.
 *
 * <p>This object represents the essential details of an eatery such as
 * its name, location, postal code, and geographical coordinates. It is
 * typically used when fetching data from APIs or adding eateries to the
 * application’s database.</p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "name": "Green Bowl Café",
 *   "buildingName": "NTU South Spine",
 *   "address": "50 Nanyang Avenue, Singapore 639798",
 *   "postalCode": "639798",
 *   "description": "A cozy café offering vegetarian and healthy meals.",
 *   "latitude": 1.3483,
 *   "longitude": 103.6831
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.controller.EateryController
 * @see com.FeedEmGreens.HealthyAura.service.EateryService
 *
 * @author Rusha
 * @version 1.0
 * @since 2025-11-07
 */
public class EateryRequest {

    /** The display name of the eatery. */
    private String name;

    /** The name of the building where the eatery is located (optional). */
    private String buildingName;

    /** The full address of the eatery, including street and unit number. */
    private String address;

    /** The postal code of the eatery location. */
    private String postalCode;

    /** A short description or summary of the eatery. */
    private String description;

    /** The latitude coordinate of the eatery's location. */
    private double latitude;

    /** The longitude coordinate of the eatery's location. */
    private double longitude;

    /**
     * Retrieves the name of the eatery.
     *
     * @return the name of the eatery
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the eatery.
     *
     * @param name the eatery’s name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the building name of the eatery (if applicable).
     *
     * @return the building name
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Sets the building name of the eatery.
     *
     * @param buildingName the building name where the eatery is located
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Retrieves the full address of the eatery.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the full address of the eatery.
     *
     * @param address the street or full address of the eatery
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retrieves the postal code of the eatery.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code of the eatery.
     *
     * @param postalCode the postal code of the eatery location
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Retrieves the description of the eatery.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a short description or summary of the eatery.
     *
     * @param description a description of the eatery
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the latitude coordinate of the eatery.
     *
     * @return the latitude value
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate of the eatery.
     *
     * @param latitude the latitude coordinate
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Retrieves the longitude coordinate of the eatery.
     *
     * @return the longitude value
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate of the eatery.
     *
     * @param longitude the longitude coordinate
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
