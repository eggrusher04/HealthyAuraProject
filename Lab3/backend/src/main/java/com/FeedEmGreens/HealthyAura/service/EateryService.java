package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.dto.AddTagsRequest;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.entity.DietaryTags;
import com.FeedEmGreens.HealthyAura.repository.EateryRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EateryService {
    private static final String DATASET_ID = "d_2925c2ccf75d1c135c2d469e0de3cee6";
    private static final String URL = "https://api-open.data.gov.sg/v1/public/api/datasets/";
    private final HttpClient client = HttpClient.newHttpClient();
    private List<EateryRequest> cachedEateries = new ArrayList<>();

    @Autowired
    private EateryRepository eateryRepository;

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


    // Convert EateryRequest DTO to Eatery entity
    public Eatery convertToEntity(EateryRequest request) {
        Eatery eatery = new Eatery();
        eatery.setName(request.getName());
        eatery.setBuildingName(request.getBuildingName());
        eatery.setAddress(request.getAddress());
        eatery.setDescription(request.getDescription());
        eatery.setLatitude(request.getLatitude());
        eatery.setLongitude(request.getLongitude());

        // Convert postal code from String to Long
        try {
            if (request.getPostalCode() != null && !request.getPostalCode().trim().isEmpty()) {
                eatery.setPostalCode(Long.parseLong(request.getPostalCode()));
            }
        } catch (NumberFormatException e) {
            // Handle invalid postal code format
            System.err.println("Invalid postal code format: " + request.getPostalCode());
        }

        return eatery;
    }

    // Save API data to database
    public List<Eatery> saveEateriesFromApi() {
        List<EateryRequest> apiEateries = fetchEateries();
        List<Eatery> savedEateries = new ArrayList<>();

        for (EateryRequest request : apiEateries) {
            Eatery entity = convertToEntity(request);
            // Check if eatery already exists by name and location
            List<Eatery> existing = eateryRepository.findByNameAndLatitudeAndLongitude(
                entity.getName(), entity.getLatitude(), entity.getLongitude());

            if (existing.isEmpty()) {
                savedEateries.add(eateryRepository.save(entity));
            }
        }

        return savedEateries;
    }

    // Get all eateries from database
    public List<Eatery> getAllEateriesFromDatabase() {
        return eateryRepository.findAll();
    }

    // Attach tags to an eatery
    public Eatery addTagsToEatery(Long eateryId, List<String> tags) {
        Eatery eatery = eateryRepository.findById(eateryId)
                .orElseThrow(() -> new IllegalArgumentException("Eatery not found: " + eateryId));

        for (String tagName : tags) {
            if (tagName == null || tagName.isBlank()) continue;
            DietaryTags tag = new DietaryTags(tagName.trim());
            eatery.addDietaryTag(tag);
        }

        return eateryRepository.save(eatery);
    }

    private String extractProperty(JSONObject props, String key){
        try{
            String descHtml = props.getString("Description");
            String pattern = "<th>" + key + "<\\/th> <td>(.*?)<\\/td>";
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(descHtml);
            return matcher.find() ? matcher.group(1).trim() : "";
        }catch(Exception e){
            return "";
        }
    }

    public List<EateryRequest> searchEatery(String query){
        if(cachedEateries.isEmpty()){
            fetchEateries();
        }

        if(query == null || query.isEmpty()){
            return cachedEateries;
        }

        String lowerCaseQuery = query.toLowerCase();

        return cachedEateries.stream()
                .filter(e -> e.getName().toLowerCase().contains(lowerCaseQuery) ||
                        e.getBuildingName().toLowerCase().contains(lowerCaseQuery) ||
                        e.getAddress().toLowerCase().contains((lowerCaseQuery)) ||
                        e.getPostalCode().contains(lowerCaseQuery) )
                .collect(Collectors.toList());
    }

    public List<Eatery> searchEateryFromDatabase(String query){
        if(query == null || query.isBlank()){
            return eateryRepository.findAll();
        }
        return eateryRepository.searchByQuery(query);
    }

    public Optional<Eatery> getEateryById(Long id) {
        return eateryRepository.findById(id);
    }

}
