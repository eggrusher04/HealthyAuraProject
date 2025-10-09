package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dietary_tags")
public class DietaryTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag", length = 100, nullable = false)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatery_id")
    private Eatery eatery;

    // Constructors
    public DietaryTags() {}

    public DietaryTags(String tag) {
        this.tag = tag;
    }

    public DietaryTags(String tag, Eatery eatery) {
        this.tag = tag;
        this.eatery = eatery;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public Eatery getEatery() { return eatery; }
    public void setEatery(Eatery eatery) { this.eatery = eatery; }

    @Override
    public String toString() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DietaryTags that = (DietaryTags) obj;
        return tag != null ? tag.equals(that.tag) : that.tag == null;
    }

}
