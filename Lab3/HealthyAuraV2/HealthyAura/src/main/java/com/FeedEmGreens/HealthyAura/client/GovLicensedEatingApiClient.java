package com.FeedEmGreens.HealthyAura.client;

import com.FeedEmGreens.HealthyAura.model.GovLicensedEatingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Component
public class GovLicensedEatingApiClient {

    // ✅ Correct resource_id with 'd_' prefix
    private static final String API_URL =
            "https://data.gov.sg/api/action/datastore_search?resource_id=d_227473e811b09731e64725f140b77697&limit=500";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<GovLicensedEatingResponse.Record> fetchEateries() {
        ResponseEntity<GovLicensedEatingResponse> response =
                restTemplate.getForEntity(API_URL, GovLicensedEatingResponse.class);

        if (response.getBody() == null || response.getBody().getResult() == null) {
            throw new RuntimeException("Failed to fetch data: Empty response from NEA API");
        }

        return response.getBody().getResult().getRecords();
    }
}
