package com.FeedEmGreens.HealthyAura.controller;


import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.dto.AddTagsRequest;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.service.EateryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for managing eatery-related operations.
 *
 * <p>This controller handles both external API data retrieval and internal
 * database management for eateries. It provides endpoints for searching,
 * syncing, and tagging eateries.</p>
 *
 * <p>Administrative endpoints (tag management) are protected and require
 * users with the <strong>ADMIN</strong> role.</p>
 *
 * <p>Typical use cases include:
 * <ul>
 *     <li>Fetching and displaying eateries to users</li>
 *     <li>Synchronizing data from an external API</li>
 *     <li>Tagging eateries for search and categorization</li>
 * </ul>
 * </p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/api/eateries")
public class EateryController {

    /**
     * Service layer that provides operations related to eateries,
     * including fetching, searching, and tag management.
     */
    @Autowired
    private EateryService eateryService;


    /**
     * Retrieves eateries from an external data source (e.g., OneMap API).
     *
     * <p>If a query string is provided, the result is filtered accordingly;
     * otherwise, all available eateries are fetched from the external API.</p>
     *
     * @param query optional search keyword to filter eateries by name or location
     * @return a {@link ResponseEntity} containing a list of {@link EateryRequest} DTOs
     */
    // Get eateries from external API
    @GetMapping("/api-data")
    public ResponseEntity<List<EateryRequest>> getEateriesFromApi(@RequestParam(required = false) String query){
        if(query == null || query.isBlank()) {
            return ResponseEntity.ok(eateryService.fetchEateries());
        }
        else{
            List<EateryRequest> results = eateryService.searchEatery(query);
            return ResponseEntity.ok(results);
        }
    }

    /**
     * Retrieves eateries stored in the application database.
     *
     * <p>The method supports multiple search filters:
     * <ul>
     *     <li>No parameters → returns all eateries</li>
     *     <li>Query only → searches by name or address</li>
     *     <li>Tags only → filters by matching tags</li>
     *     <li>Query + Tags → combined search for more refined results</li>
     * </ul>
     * </p>
     *
     * @param query optional search string to match eatery names or locations
     * @param tags  optional list of tags used to filter eateries
     * @return a {@link ResponseEntity} containing a list of {@link Eatery} entities
     */

    // Get eateries from database (returns entities)
    @GetMapping("/fetchDb")
    public ResponseEntity<List<Eatery>> getAllEateries(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> tags) {

        List<Eatery> eateries;

        if ((query == null || query.isBlank()) && (tags == null || tags.isEmpty())) {
            eateries = eateryService.getAllEateriesFromDatabase();
        } else if (tags == null || tags.isEmpty()) {
            eateries = eateryService.searchEateryFromDatabase(query);
        } else if (query == null || query.isBlank()) {
            eateries = eateryService.searchEateryByTags(tags);
        } else {
            eateries = eateryService.searchEateryByQueryAndTags(query, tags);
        }

        return ResponseEntity.ok(eateries);
    }

    /**
     * Retrieves a single {@link Eatery} by its unique ID.
     *
     * <p>Used to fetch detailed information about a specific eatery
     * such as its address, tags, or crowd data.</p>
     *
     * @param id the unique identifier of the eatery
     * @return a {@link ResponseEntity} containing the eatery, if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Eatery> getEateryById(@PathVariable Long id) {
        return ResponseEntity.of(
                eateryService.getEateryById(id) // returns Optional<Eatery>
        );
    }

    /**
     * Synchronizes eateries from the external API into the local database.
     *
     * <p>This operation fetches all available eateries from the remote source
     * and saves any new entries into the local system, ensuring data consistency.</p>
     *
     * @return a {@link ResponseEntity} containing the list of saved {@link Eatery} records
     */
    // Sync API data to database
    @PostMapping("/sync")
    public ResponseEntity<List<Eatery>> syncEateriesFromApi(){
        List<Eatery> savedEateries = eateryService.saveEateriesFromApi();
        return ResponseEntity.ok(savedEateries);
    }

    /**
     * Adds one or more tags to a specific eatery.
     *
     * <p>This endpoint allows administrators to categorize eateries
     * (e.g., <code>Vegetarian</code>, <code>Halal</code>, <code>Low-Calorie</code>)
     * to improve search filtering and recommendations.</p>
     *
     * @param eateryId the ID of the eatery to modify
     * @param request  the {@link AddTagsRequest} containing a list of new tags
     * @return a {@link ResponseEntity} containing the updated {@link Eatery}
     */
    // Add tags to an eatery
    @PostMapping("/{eateryId}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Eatery> addTags(
            @PathVariable Long eateryId,
            @RequestBody AddTagsRequest request
    ){
        Eatery updated = eateryService.addTagsToEatery(eateryId, request.getTags());
        return ResponseEntity.ok(updated);
    }

    /**
     * Removes a specific tag from an eatery.
     *
     * <p>This is typically used by administrators when a tag
     * is no longer relevant or incorrectly assigned.</p>
     *
     * @param eateryId the ID of the eatery to modify
     * @param tag      the tag to remove
     * @return a {@link ResponseEntity} containing the updated {@link Eatery}
     */
    // Delete a tag from an eatery
    @DeleteMapping("/{eateryId}/tags/{tag}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Eatery> deleteTag(
            @PathVariable Long eateryId,
            @PathVariable String tag
    ){
        Eatery updated = eateryService.deleteTagFromEatery(eateryId, tag);
        return ResponseEntity.ok(updated);
    }

    /**
     * Edits or renames an existing tag for a specific eatery.
     *
     * <p>This allows administrators to maintain consistent tag naming conventions
     * or correct typos without removing and re-adding tags manually.</p>
     *
     * @param eateryId the ID of the eatery to update
     * @param request  the request object containing the old and new tag values
     * @return a {@link ResponseEntity} containing the updated {@link Eatery}
     */
    // Edit/rename a tag for an eatery
    @PutMapping("/{eateryId}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Eatery> editTag(
            @PathVariable Long eateryId,
            @RequestBody com.FeedEmGreens.HealthyAura.dto.UpdateTagRequest request
    ){
        Eatery updated = eateryService.editTagForEatery(eateryId, request.getOldTag(), request.getNewTag());
        return ResponseEntity.ok(updated);
    }
}
