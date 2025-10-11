package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.ProfileResponse;
import com.FeedEmGreens.HealthyAura.dto.UpdateProfileRequest;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.ProfileService;
import org.springframework.http.ResponseEntity;
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

    // 🟩 1️⃣ Get profile by username (for now, via query param)
    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String username) {
        Users user = profileService.getUserProfile(username);

        ProfileResponse response = new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPoints(),
                user.getPreferences()
        );

        return ResponseEntity.ok(response);
    }

    // 🟦 2️⃣ Update preferences for a given username
    @PutMapping("/{username}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable String username,
            @RequestBody UpdateProfileRequest req
    ) {
        profileService.updatePreferences(username, req.getPreferences());

        Users updatedUser = profileService.getUserProfile(username);
        ProfileResponse response = new ProfileResponse(
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getPoints(),
                updatedUser.getPreferences()
        );

        return ResponseEntity.ok(response);
    }

    // 🟨 3️⃣ Get user points (optional for rewards)
    @GetMapping("/{username}/points")
    public ResponseEntity<Map<String, Integer>> getPoints(@PathVariable String username) {
        Users user = profileService.getUserProfile(username);

        Map<String, Integer> pointsMap = new HashMap<>();
        pointsMap.put("points", user.getPoints());

        return ResponseEntity.ok(pointsMap);
    }
}
