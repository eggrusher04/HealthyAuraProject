package com.FeedEmGreens.HealthyAura.controller;


import com.FeedEmGreens.HealthyAura.dto.EateryRequest;
import com.FeedEmGreens.HealthyAura.service.EateryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/eateries")
public class EateryController {
    @Autowired
    private EateryService eateryService;

    @GetMapping
    public ResponseEntity<List<EateryRequest>> getAllEateries(){
        return ResponseEntity.ok(eateryService.fetchEateries());
    }

    @GetMapping
    public ResponseEntity<List<EateryRequest>> search(@RequestParam(required = false) String query){
        List<EateryRequest> results = eateryService.searchEatery(query);
        return ResponseEntity.ok(results);
    }
}
