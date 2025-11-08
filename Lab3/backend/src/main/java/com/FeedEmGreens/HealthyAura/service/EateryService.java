package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.entity.DietaryTags;
import com.FeedEmGreens.HealthyAura.entity.AdminActionLog;
import com.FeedEmGreens.HealthyAura.repository.EateryRepository;
import com.FeedEmGreens.HealthyAura.repository.DietaryTagsRepository;
import com.FeedEmGreens.HealthyAura.repository.AdminActionLogRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer responsible for handling all eatery-related operations
 * such as fetching, saving, searching, and tagging.
 *
 * <p>This class acts as the business logic layer that interacts with:
 * <ul>
 *   <li>External Open Data APIs (for real-time eatery data)</li>
 *   <li>Repositories (for CRUD operations in the database)</li>
 *   <li>Administrative logging (to record tag modifications by admins)</li>
 * </ul>
 * </p>
 *
 * <p>Additionally, it provides caching and tag-management utilities
 * that support search, filtering, and user personalization features
 * within the HealthyAura system.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.Eatery
 * @see com.FeedEmGreens.HealthyAura.entity.DietaryTags
 * @see com.FeedEmGreens.HealthyAura.dto.EateryRequest
 * @see com.FeedEmGreens.HealthyAura.repository.EateryRepository
 * @see com.FeedEmGreens.HealthyAura.repository.AdminActionLogRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class EateryService {

    /** Open Data Singapore dataset ID for the healthier eateries API. */
    private static final String DATASET_ID = "d_2925c2ccf75d1c135c2d469e0de3cee6";

    /** Base URL for the data.gov.sg public API. */
    private static final String URL = "https://api-open.data.gov.sg/v1/public/api/datasets/";

    /** Shared HTTP client for performing API requests. */
    private final HttpClient client = HttpClient.newHttpClient();

    /** Cached in-memory list of eateries fetched from the external API. */
    private List<EateryRequest> cachedEateries = new ArrayList<>();

    @Autowired
    private EateryRepository eateryRepository;

    @Autowired
    private DietaryTagsRepository dietaryTagsRepository;

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    /**
     * Fetches and parses real-time eatery data from Singapore’s Open Data API.
     *
     * <p>The method follows a two-step process:
     * <ol>
     *   <li>Polls the API for the current dataset download URL.</li>
     *   <li>Fetches and parses the actual dataset JSON.</li>
     * </ol>
     * </p>
     *
     * <p>Each record is converted into an {@link EateryRequest} DTO containing
     * metadata such as name, address, postal code, and geolocation coordinates.</p>
     *
     * @return a list of parsed {@link EateryRequest} objects
     * @throws RuntimeException if the API call or parsing fails
     */
    public List<EateryRequest> fetchEateries() {
        List<EateryRequest> eateries = new ArrayList<>();

        try {
            HttpRequest pollRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL + DATASET_ID + "/poll-download"))
                    .build();

            HttpResponse<String> pollResponse = client.send(pollRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject pollJson = new JSONObject(pollResponse.body());

            if (pollJson.getInt("code") != 0) {
                throw new RuntimeException("Failed to fetch poll-download data");
            }

            String fetchUrl = pollJson.getJSONObject("data").getString("url");

            HttpRequest dataReq = HttpRequest.newBuilder().uri(URI.create(fetchUrl)).build();
            HttpResponse<String> dataResponse = client.send(dataReq, HttpResponse.BodyHandlers.ofString());

            JSONObject dataJson = new JSONObject(dataResponse.body());
            JSONArray jsonFeature = dataJson.getJSONArray("features");

            for (int i = 0; i < jsonFeature.length(); i++) {
                JSONObject features = jsonFeature.getJSONObject(i);
                JSONObject geometry = features.getJSONObject("geometry");
                JSONObject properties = features.getJSONObject("properties");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                EateryRequest eatery = new EateryRequest();
                eatery.setName(extractProperty(properties, "NAME"));
                eatery.setBuildingName(extractProperty(properties, "ADDRESSBUILDINGNAME"));
                eatery.setAddress(extractProperty(properties, "ADDRESSSTREETNAME"));
                eatery.setPostalCode(extractProperty(properties, "ADDRESSPOSTALCODE"));
                eatery.setDescription(extractProperty(properties, "DESCRIPTION"));
                eatery.setLatitude(coordinates.getDouble(1));
                eatery.setLongitude(coordinates.getDouble(0));

                eateries.add(eatery);
            }

            this.cachedEateries = eateries;
            return eateries;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch eatery data");
        }
    }

    /**
     * Converts an {@link EateryRequest} DTO into an {@link Eatery} entity object.
     *
     * @param request the DTO containing API or frontend-submitted eatery details
     * @return the converted {@link Eatery} entity ready for persistence
     */
    public Eatery convertToEntity(EateryRequest request) {
        Eatery eatery = new Eatery();
        eatery.setName(request.getName());
        eatery.setBuildingName(request.getBuildingName());
        eatery.setAddress(request.getAddress());
        eatery.setDescription(request.getDescription());
        eatery.setLatitude(request.getLatitude());
        eatery.setLongitude(request.getLongitude());

        try {
            if (request.getPostalCode() != null && !request.getPostalCode().trim().isEmpty()) {
                eatery.setPostalCode(Long.parseLong(request.getPostalCode()));
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid postal code format: " + request.getPostalCode());
        }

        return eatery;
    }

    /**
     * Saves newly fetched eateries into the database while avoiding duplicates.
     *
     * <p>Duplicate checking is performed by comparing name and geolocation coordinates.</p>
     *
     * @return a list of successfully saved {@link Eatery} entities
     */
    public List<Eatery> saveEateriesFromApi() {
        List<EateryRequest> apiEateries = fetchEateries();
        List<Eatery> savedEateries = new ArrayList<>();

        for (EateryRequest request : apiEateries) {
            Eatery entity = convertToEntity(request);
            List<Eatery> existing = eateryRepository.findByNameAndLatitudeAndLongitude(
                    entity.getName(), entity.getLatitude(), entity.getLongitude());

            if (existing.isEmpty()) {
                savedEateries.add(eateryRepository.save(entity));
            }
        }

        return savedEateries;
    }

    /** Retrieves all eateries stored in the database. */
    public List<Eatery> getAllEateriesFromDatabase() {
        return eateryRepository.findAll();
    }

    /**
     * Adds one or more dietary tags to a specific eatery.
     *
     * @param eateryId the target eatery’s ID
     * @param tags a list of dietary tag names to attach
     * @return the updated {@link Eatery} entity after tag assignment
     */
    public Eatery addTagsToEatery(Long eateryId, List<String> tags) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        List<String> existing = eatery.getTagNames().stream()
                .map(s -> s.toLowerCase().trim())
                .toList();

        int added = 0;
        for (String tagName : tags) {
            if (tagName == null || tagName.isBlank()) continue;
            String normalized = tagName.trim();
            if (existing.contains(normalized.toLowerCase())) continue;

            DietaryTags tag = new DietaryTags(normalized);
            eatery.addDietaryTag(tag);
            added++;
        }

        Eatery saved = eateryRepository.save(eatery);
        logAdminAction("ADD_TAG", eateryId, null,
                added > 0 ? "Added " + added + " tag(s)" : "No tags added (duplicates/blank)");
        return saved;
    }

    /**
     * Deletes a specific tag from an eatery.
     *
     * @param eateryId the eatery to modify
     * @param tagName the tag name to remove (case-insensitive)
     * @return the updated {@link Eatery} entity
     */
    public Eatery deleteTagFromEatery(Long eateryId, String tagName) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be blank");
        }

        DietaryTags existing = dietaryTagsRepository.findByEateryAndTagIgnoreCase(eatery, tagName.trim())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found for eatery: " + tagName));

        eatery.removeDietaryTag(existing);
        Eatery saved = eateryRepository.save(eatery);
        logAdminAction("DELETE_TAG", eateryId, existing.getId(),
                "Removed tag '" + existing.getTag() + "'");
        return saved;
    }

    /**
     * Renames an existing tag on an eatery.
     *
     * @param eateryId the eatery’s ID
     * @param oldTagName the current tag name
     * @param newTagName the desired new tag name
     * @return the updated {@link Eatery} entity
     */
    public Eatery editTagForEatery(Long eateryId, String oldTagName, String newTagName) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        if (oldTagName == null || oldTagName.isBlank() || newTagName == null || newTagName.isBlank()) {
            throw new IllegalArgumentException("Old and new tag names must be provided");
        }

        DietaryTags existing = dietaryTagsRepository.findByEateryAndTagIgnoreCase(eatery, oldTagName.trim())
                .orElseThrow(() -> new IllegalArgumentException("Tag not found for eatery: " + oldTagName));

        boolean duplicateExists = eatery.getTagNames().stream()
                .map(String::toLowerCase)
                .anyMatch(s -> s.equals(newTagName.trim().toLowerCase()));

        if (duplicateExists) {
            throw new IllegalArgumentException("A tag with the same name already exists for this eatery");
        }

        String before = existing.getTag();
        existing.setTag(newTagName.trim());
        Eatery saved = eateryRepository.save(eatery);

        logAdminAction("EDIT_TAG", eateryId, existing.getId(),
                "Renamed tag '" + before + "' -> '" + newTagName + "'");
        return saved;
    }

    /** Records admin actions for auditing and moderation transparency. */
    private void logAdminAction(String actionType, Long eateryId, Long targetId, String details) {
        String admin = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "unknown";
        AdminActionLog log = new AdminActionLog(admin, actionType, "TAG", targetId, eateryId, details,
                java.time.LocalDateTime.now());
        adminActionLogRepository.save(log);
    }

    /** Extracts property values from HTML-like description in the Open Data API response. */
    private String extractProperty(JSONObject props, String key) {
        try {
            String descHtml = props.getString("Description");
            String pattern = "<th>" + key + "<\\/th> <td>(.*?)<\\/td>";
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(descHtml);
            return matcher.find() ? matcher.group(1).trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    /** Performs a keyword search across cached in-memory eateries (from API). */
    public List<EateryRequest> searchEatery(String query) {
        if (cachedEateries.isEmpty()) fetchEateries();
        if (query == null || query.isEmpty()) return cachedEateries;

        String lowerCaseQuery = query.toLowerCase();
        return cachedEateries.stream()
                .filter(e -> e.getName().toLowerCase().contains(lowerCaseQuery)
                        || e.getBuildingName().toLowerCase().contains(lowerCaseQuery)
                        || e.getAddress().toLowerCase().contains(lowerCaseQuery)
                        || e.getPostalCode().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }

    /** Performs a keyword search across eateries stored in the database. */
    public List<Eatery> searchEateryFromDatabase(String query) {
        if (query == null || query.isBlank()) return eateryRepository.findAll();
        return eateryRepository.searchByQuery(query);
    }

    /** Retrieves a single eatery by its unique identifier. */
    public Optional<Eatery> getEateryById(Long id) {
        return eateryRepository.findById(id);
    }

    /** Filters eateries based on dietary tags. */
    public List<Eatery> searchEateryByTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return eateryRepository.findAll();

        return eateryRepository.findAll().stream()
                .filter(e -> e.getDietaryTags() != null && !e.getDietaryTags().isEmpty()
                        && e.getDietaryTags().stream()
                        .anyMatch(tag -> tags.stream()
                                .anyMatch(input -> tag.getTag().equalsIgnoreCase(input))))
                .collect(Collectors.toList());
    }

    /** Performs combined search by name/address and dietary tags. */
    public List<Eatery> searchEateryByQueryAndTags(String query, List<String> tags) {
        String lowerQuery = query != null ? query.toLowerCase() : "";
        return eateryRepository.findAll().stream()
                .filter(e -> {
                    boolean matchesQuery = query == null || query.isBlank()
                            || e.getName().toLowerCase().contains(lowerQuery)
                            || (e.getAddress() != null && e.getAddress().toLowerCase().contains(lowerQuery));

                    boolean matchesTags = tags == null || tags.isEmpty()
                            || (e.getDietaryTags() != null && e.getDietaryTags().stream()
                            .anyMatch(tag -> tags.stream()
                                    .anyMatch(input -> tag.getTag().equalsIgnoreCase(input))));

                    return matchesQuery && matchesTags;
                })
                .collect(Collectors.toList());
    }
}
