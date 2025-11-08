package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.CrowdDisplayDTO;
import com.FeedEmGreens.HealthyAura.service.CrowdService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for providing crowd level information for eateries.
 *
 * <p>This controller exposes an endpoint for retrieving real-time or
 * computed crowd status data for a specific eatery, based on its ID.
 * The data is typically displayed in the frontend to help users
 * make informed dining decisions (e.g., choosing less crowded stalls).</p>
 *
 * <p>Requests are handled through {@link CrowdService}, which processes
 * backend logic and returns a {@link CrowdDisplayDTO} containing the
 * formatted crowd information.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/crowd")
public class CrowdController {

    /**
     * Service layer responsible for retrieving and computing crowd data.
     */
    private final CrowdService crowdService;

    /**
     * Constructs a {@code CrowdController} with a required {@link CrowdService}.
     *
     * @param crowdService the service used to retrieve crowd information
     */
    public CrowdController(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    /**
     * Retrieves the current crowd information for a specific eatery.
     *
     * <p>This endpoint is accessed via <code>GET /crowd/{eateryId}</code>
     * and returns a {@link CrowdDisplayDTO} object that includes details
     * such as the estimated crowd level, last updated time, and
     * occupancy status.</p>
     *
     * @param eateryId the unique identifier of the eatery
     * @return a {@link CrowdDisplayDTO} containing the crowd information for the eatery
     */
    @GetMapping("/{eateryId}")
    public CrowdDisplayDTO getCrowdInfo(@PathVariable Long eateryId) {
        return crowdService.getCrowdStatus(eateryId);
    }
}
