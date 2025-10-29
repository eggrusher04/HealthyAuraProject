package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.CrowdDisplayDTO;
import com.FeedEmGreens.HealthyAura.service.CrowdService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crowd")
public class CrowdController {

    private final CrowdService crowdService;

    public CrowdController(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    @GetMapping("/{eateryId}")
    public CrowdDisplayDTO getCrowdInfo(@PathVariable Long eateryId) {
        return crowdService.getCrowdStatus(eateryId);
    }
}
