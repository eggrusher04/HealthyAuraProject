package com.FeedEmGreens.HealthyAura.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) used for adding one or more tags to an eatery.
 *
 * <p>This object is typically sent in a JSON request body when an admin
 * or authorized user adds descriptive tags (e.g., "Halal", "Vegan", "Low Calorie")
 * to a specific eatery for improved search and filtering.</p>
 *
 * <p>Example JSON request:</p>
 * <pre>
 * {
 *   "tags": ["Healthy", "Vegetarian", "Budget-Friendly"]
 * }
 * </pre>
 *
 * @version 1.0
 * @since 2025-11-07
 */

public class AddTagsRequest {
    /**
     * List of tags to be added to an eatery.
     */
	private List<String> tags;

    /**
     * Retrieves the list of tags.
     *
     * @return a list of tag strings
     */
	public List<String> getTags() {
		return tags;
	}

    /**
     * Sets the list of tags to be added.
     *
     * @param tags a list of tag strings
     */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}