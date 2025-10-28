package com.FeedEmGreens.HealthyAura.controller;


import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.dto.AddTagsRequest;
import com.FeedEmGreens.HealthyAura.entity.Eatery;
import com.FeedEmGreens.HealthyAura.service.EateryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eateries")
public class EateryController {
    @Autowired
    private EateryService eateryService;

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

    // Get eateries from database (returns entities)
    @GetMapping("/fetchDb")
    public ResponseEntity<List<Eatery>> getAllEateries(@RequestParam(required = false) String query){
        List<Eatery> eateries;

        if(query == null || query.isBlank()){
            return ResponseEntity.ok(eateryService.getAllEateriesFromDatabase());
        }
        else{
            eateries = eateryService.searchEateryFromDatabase(query);
        }

        return ResponseEntity.ok(eateries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Eatery> getEateryById(@PathVariable Long id) {
        return ResponseEntity.of(
                eateryService.getEateryById(id) // returns Optional<Eatery>
        );
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
}
