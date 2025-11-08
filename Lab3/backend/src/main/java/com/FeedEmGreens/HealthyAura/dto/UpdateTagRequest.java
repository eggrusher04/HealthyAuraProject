package com.FeedEmGreens.HealthyAura.dto;

/**
 * Data Transfer Object (DTO) used for updating or renaming a tag
 * associated with an eatery in the database.
 *
 * <p>This class carries the old tag name and the new tag name that will
 * replace it. It is typically used by the
 * {@link com.FeedEmGreens.HealthyAura.controller.EateryController}
 * through the <code>/api/eateries/{eateryId}/tags</code> endpoint.</p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "oldTag": "Vegetarian",
 *   "newTag": "Plant-Based"
 * }
 * </pre>
 *
 * @see com.FeedEmGreens.HealthyAura.controller.EateryController
 * @see com.FeedEmGreens.HealthyAura.service.EateryService
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 *
 * @version 1.0
 * @since 2025-11-07
 */
public class UpdateTagRequest {

    /** The existing tag name that will be replaced. */
    private String oldTag;

    /** The new tag name that will replace the existing one. */
    private String newTag;

    /**
     * Returns the current tag name that needs to be updated.
     *
     * @return the old tag name
     */
    public String getOldTag() {
        return oldTag;
    }

    /**
     * Sets the current tag name that will be replaced.
     *
     * @param oldTag the old tag name
     */
    public void setOldTag(String oldTag) {
        this.oldTag = oldTag;
    }

    /**
     * Returns the new tag name to replace the old tag.
     *
     * @return the new tag name
     */
    public String getNewTag() {
        return newTag;
    }

    /**
     * Sets the new tag name that will replace the old one.
     *
     * @param newTag the new tag name
     */
    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }
}
