package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity representing a dietary tag associated with an {@link Eatery}.
 *
 * <p>Each {@code DietaryTags} record stores a descriptive label (e.g., “Vegan”,
 * “Halal”, “Low Sugar”) linked to a specific eatery. These tags are used to
 * enhance search, filtering, and personalized recommendation functionalities
 * within the HealthyAura platform.</p>
 *
 * <p>Entries are persisted in the <b>dietary_tags</b> database table.</p>
 *
 * <p>Example usage:</p>
 * <ul>
 *   <li>Associating an eatery with tags such as “Vegetarian”, “Gluten-Free”</li>
 *   <li>Filtering eateries by user dietary preferences</li>
 *   <li>Displaying tags in the eatery’s profile card or recommendation list</li>
 * </ul>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.service.EateryService
 * @see com.FeedEmGreens.HealthyAura.controller.EateryController
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Entity
@Table(name = "dietary_tags")
public class DietaryTags {

    /** Unique identifier for the dietary tag. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The textual label of the dietary tag (e.g., “Vegan”, “Halal”). */
    @Column(name = "tag", length = 100, nullable = false)
    private String tag;

    /**
     * The associated {@link Eatery} entity that this tag belongs to.
     * <p>Marked as {@code @JsonIgnore} to prevent circular references
     * during JSON serialization.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_id")
    @JsonIgnore
    private Eatery eatery;

    /** Default no-argument constructor for JPA. */
    public DietaryTags() {}

    /**
     * Constructs a {@code DietaryTags} instance with the given tag name.
     *
     * @param tag the name of the dietary tag
     */
    public DietaryTags(String tag) {
        this.tag = tag;
    }

    /**
     * Constructs a {@code DietaryTags} instance with the given tag name
     * and associated eatery.
     *
     * @param tag the name of the dietary tag
     * @param eatery the {@link Eatery} entity this tag is linked to
     */
    public DietaryTags(String tag, Eatery eatery) {
        this.tag = tag;
        this.eatery = eatery;
    }

    /** @return the unique identifier of the dietary tag */
    public Long getId() { return id; }

    /** @param id sets the unique identifier of the tag */
    public void setId(Long id) { this.id = id; }

    /** @return the tag label (e.g., “Vegan”, “Low Fat”) */
    public String getTag() { return tag; }

    /** @param tag sets the tag label */
    public void setTag(String tag) { this.tag = tag; }

    /** @return the {@link Eatery} entity linked to this tag */
    public Eatery getEatery() { return eatery; }

    /** @param eatery sets the {@link Eatery} entity linked to this tag */
    public void setEatery(Eatery eatery) { this.eatery = eatery; }

    /**
     * Returns the tag string when this entity is printed or logged.
     *
     * @return the tag name
     */
    @Override
    public String toString() {
        return tag;
    }

    /**
     * Compares two {@code DietaryTags} objects for equality based on their tag value.
     *
     * @param obj the object to compare with
     * @return {@code true} if the tag names are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DietaryTags that = (DietaryTags) obj;
        return tag != null ? tag.equals(that.tag) : that.tag == null;
    }
}
