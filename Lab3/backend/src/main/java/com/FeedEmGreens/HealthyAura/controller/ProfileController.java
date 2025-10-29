package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.ProfileResponse;
import com.FeedEmGreens.HealthyAura.dto.UpdateProfileRequest;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RestController
// CHANGED: Renamed the base path to avoid conflict with the new UserController's /profile/me endpoint.
// If this controller is only for profile updates, it can stay, but if all these update endpoints
// are also in UserController, this entire file should be deleted.
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    private int getUserTotalPoints(Users user) {
        return (user.getPoints() != null) ? user.getPoints().getTotalPoints() : 0;
    }

    // REMOVED: This method caused the Ambiguous Mapping error because UserController also has a GET /profile/me mapping.
    /*
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Users user) {
        // ... Removed method to resolve Ambiguous mapping error.
    }
    */

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

    @GetMapping("/debug/encode")
    public String encodePassword(@RequestParam String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @GetMapping("/debug/test-password")
    public String testPassword(@RequestParam String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV6UiC";
        boolean matches = encoder.matches(password, storedHash);
        return "Password: " + password + " | Matches: " + matches;
    }

    @PutMapping("/me/email")
    public ResponseEntity<Map<String, String>> updateEmail(@RequestBody Map<String, String> req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newEmail = req.get("email");

        Users updatedUser = profileService.updateEmail(username, newEmail);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email updated successfully");
        response.put("email", updatedUser.getEmail());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody Map<String, String> req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String newPassword = req.get("password");

        profileService.updatePassword(username, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return ResponseEntity.ok(response);
    }
}