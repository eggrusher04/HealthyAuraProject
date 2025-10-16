package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.client.GovLicensedEatingApiClient;
import com.FeedEmGreens.HealthyAura.model.GovLicensedEatingResponse;
import com.FeedEmGreens.HealthyAura.util.EateryDataCleaner;
import com.FeedEmGreens.HealthyAura.mapper.GovLicensedEatingMapper;
import com.FeedEmGreens.HealthyAura.dto.EateryDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Temporary test controller to verify the NEA API integration.
 * You can hit this endpoint directly to confirm your data pipeline works.
 */
@RestController
@RequestMapping("/test")
public class GovApiTestController {

    private final GovLicensedEatingApiClient apiClient;

    public GovApiTestController(GovLicensedEatingApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Calls the NEA API, cleans data, maps to DTOs, and returns the first 10 records.
     */
    @GetMapping("/eateries")
    public ResponseEntity<?> testGovApi() {
        try {
            System.out.println("\n=== [NEA API TEST STARTED] ===");

            // Step 1: Fetch raw government data
            List<GovLicensedEatingResponse.Record> rawRecords = apiClient.fetchEateries();
            System.out.println("Fetched " + rawRecords.size() + " records from NEA API");

            // Step 2: Clean and filter records
            List<GovLicensedEatingResponse.Record> cleanedRecords = EateryDataCleaner.clean(rawRecords);
            System.out.println("Cleaned dataset down to " + cleanedRecords.size() + " valid records");

            // Step 3: Map to DTOs
            List<EateryDTO> dtoList = cleanedRecords.stream()
                    .map(GovLicensedEatingMapper::toDTO)
                    .limit(10)
                    .collect(Collectors.toList());

            System.out.println("Mapped " + dtoList.size() + " records to DTOs");
            System.out.println("=== [NEA API TEST SUCCESS] ===\n");

            // Step 4: Return as JSON
            return ResponseEntity.ok(dtoList);

        } catch (Exception e) {
            System.err.println("❌ Error during NEA API test: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Failed to fetch or process NEA data: " + e.getMessage() + "\"}");
        }
    }
}
