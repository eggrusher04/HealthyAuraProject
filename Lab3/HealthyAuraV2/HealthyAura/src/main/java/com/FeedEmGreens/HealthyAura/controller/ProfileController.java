package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.ProfileResponse;
import com.FeedEmGreens.HealthyAura.dto.UpdateProfileRequest;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    private int getUserTotalPoints(Users user) {
        return (user.getPoints() != null) ? user.getPoints().getTotalPoints() : 0;
    }

    // Get profile by username (for now, via query param)
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = profileService.getUserProfile(username);

        // Safely extract total points
        int totalPoints = getUserTotalPoints(user);

        ProfileResponse response = new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                totalPoints,
                user.getPreferences()
        );

        return ResponseEntity.ok(response);
    }

    // Update preferences for a given username
    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest req
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        profileService.updatePreferences(username, req.getPreferences());

        Users updatedUser = profileService.getUserProfile(username);

        int totalPoints = getUserTotalPoints(updatedUser);


        ProfileResponse response = new ProfileResponse(
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                totalPoints,
                updatedUser.getPreferences()
        );

        return ResponseEntity.ok(response);
    }

    // Get only user points (for rewards module)
    @GetMapping("/me/points")
    public ResponseEntity<Map<String, Integer>> getPoints() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = profileService.getUserProfile(username);

        int totalPoints = getUserTotalPoints(user);

        Map<String, Integer> pointsMap = new HashMap<>();
        pointsMap.put("points", totalPoints);

        return ResponseEntity.ok(pointsMap);
    }
}
