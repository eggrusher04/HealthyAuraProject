package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EateryService {
    private static final String DATASET_ID = "d_2925c2ccf75d1c135c2d469e0de3cee6";
    private static final String URL = "https://api-open.data.gov.sg/v1/public/api/datasets/";
    private final HttpClient client = HttpClient.newHttpClient();
    private List<EateryRequest> cachedEateries = new ArrayList<>();

    public List<EateryRequest> fetchEateries(){
        List<EateryRequest> eateries = new ArrayList<>();

        try{

            HttpRequest pollRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL + DATASET_ID + "/poll-download"))
                    .build();

            HttpResponse<String> pollResponse = client.send(pollRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject pollJson = new JSONObject(pollResponse.body());

            if(pollJson.getInt("code") != 0){
                throw new RuntimeException("Failed to fetch poll-download data");
            }

            String fetchUrl = pollJson.getJSONObject("data").getString("url");

            HttpRequest dataReq = HttpRequest.newBuilder().uri(URI.create(fetchUrl)).build();
            HttpResponse<String> dataResponse = client.send(dataReq, HttpResponse.BodyHandlers.ofString());

            JSONObject dataJson = new JSONObject(dataResponse.body());
            JSONArray jsonFeature = dataJson.getJSONArray("features");

            for(int i = 0; i < jsonFeature.length(); i++){
                JSONObject features = jsonFeature.getJSONObject(i);
                JSONObject geometry = features.getJSONObject("geometry");
                JSONObject properties = features.getJSONObject("properties");

                JSONArray coordinates = geometry.getJSONArray("coordinates");

                EateryRequest eatery = new EateryRequest();
                eatery.setName(extractProperty(properties,"NAME"));
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

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch eatery data");
        }


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
}
