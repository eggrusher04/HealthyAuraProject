package com.FeedEmGreens.HealthyAura.controller;


import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.service.EateryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eateries")
public class EateryController {
    @Autowired
    private EateryService eateryService;

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
}
