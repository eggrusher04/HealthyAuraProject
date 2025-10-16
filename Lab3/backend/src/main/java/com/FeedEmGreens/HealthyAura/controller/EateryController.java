package com.FeedEmGreens.HealthyAura.controller;


import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.dto.AddTagsRequest;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.service.EateryService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import java.util.List;

@RestController
@RequestMapping("/api/eateries")
public class EateryController {
    @Autowired
    private EateryService eateryService;

    // Unified endpoint: All or Search query optional
    @GetMapping
    public ResponseEntity<List<EateryRequest>> getAllEateries(@RequestParam(required = false) String query){
        if(query == null || query.isBlank()) {
            return ResponseEntity.ok(eateryService.fetchEateries());
        }
        else{
            List<EateryRequest> results = eateryService.searchEatery(query);
            return ResponseEntity.ok(results);
        }
        }
    // Get eateries from external API
    @GetMapping("/api-data")
    public ResponseEntity<List<EateryRequest>> getEateriesFromApi(){
        return ResponseEntity.ok(eateryService.fetchEateries());
    }

    // Get eateries from database (returns entities)
    @GetMapping("/db")
    public ResponseEntity<List<Eatery>> getAllEateriesFromDb() {
        return ResponseEntity.ok(eateryService.getAllEateriesFromDatabase());
    }

    // Sync API data to database
    @PostMapping("/sync")
    public ResponseEntity<List<Eatery>> syncEateriesFromApi(){
        List<Eatery> savedEateries = eateryService.saveEateriesFromApi();
        return ResponseEntity.ok(savedEateries);
    }

    // Add tags to an eatery
    @PostMapping("/{eateryId}/tags")
    public ResponseEntity<Eatery> addTags(
            @PathVariable Long eateryId,
            @RequestBody AddTagsRequest request
    ){
        Eatery updated = eateryService.addTagsToEatery(eateryId, request.getTags());
        return ResponseEntity.ok(updated);
    }

    // Directions backend ( not sure if we gonna add this for now )
    private static final Logger logger = LoggerFactory.getLogger(EateryController.class);

    @GetMapping("/directions")
    public ResponseEntity<Void> redirectToGoogleMaps(
            @RequestParam double destLat,
            @RequestParam double destLon,
            @RequestParam(required = false) Double originLat,
            @RequestParam(required = false) Double originLon
    ) {
        String url;
        if (originLat != null && originLon != null) {
            url = String.format(
                    "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f",
                    originLat, originLon, destLat, destLon
            );
        } else {
            url = String.format(
                    "https://www.google.com/maps/dir/?api=1&destination=%f,%f",
                    destLat, destLon
            );
            logger.info("Redirecting to Google Maps URL: {}", url);

        }
        return ResponseEntity.status(302).header("Location", url).build();
    }


}
