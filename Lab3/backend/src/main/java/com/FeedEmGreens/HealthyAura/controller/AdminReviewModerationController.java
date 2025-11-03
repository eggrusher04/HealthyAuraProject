package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.entity.AdminActionLog;
import com.FeedEmGreens.HealthyAura.entity.ReviewFlag;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.repository.AdminActionLogRepository;
import com.FeedEmGreens.HealthyAura.repository.ReviewFlagRepository;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import com.FeedEmGreens.HealthyAura.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/review-moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewModerationController {

    @Autowired
    private ReviewFlagRepository reviewFlagRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    // Get all pending flagged reviews (moderation queue)
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewFlag>> getPendingFlags() {
        List<ReviewFlag> flags = reviewService.getPendingFlags();
        return ResponseEntity.ok(flags);
    }

    // Resolve a flag (approve removal or dismiss)
    @PutMapping("/flags/{flagId}/resolve")
    public ResponseEntity<?> resolveFlag(
            @PathVariable Long flagId,
            @RequestParam String action, // "REMOVE" or "DISMISS"
            @RequestParam(required = false) String notes
    ) {
        ReviewFlag flag = reviewFlagRepository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("Flag not found: " + flagId));

        if (!flag.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Flag has already been resolved");
        }

        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Users adminUser = userRepository.findByUsername(adminUsername).orElse(null);

        flag.setStatus(action.equals("REMOVE") ? "RESOLVED" : "DISMISSED");
        flag.setAdmin(adminUser);
        flag.setAdminNotes((notes != null && !notes.isBlank())
                ? notes
                : (action + " by: " + adminUsername));
        flag.setReviewedAt(LocalDateTime.now());
        reviewFlagRepository.save(flag);

        // Log admin action for resolving a flag
        try {
            Long eateryId = flag.getReview() != null && flag.getReview().getEatery() != null
                    ? flag.getReview().getEatery().getId() : null;
            String details = "action=" + action + (notes != null && !notes.isBlank() ? "; notes=" + notes : "");
            AdminActionLog log = new AdminActionLog(adminUsername, "FLAG_RESOLVE", "FLAG", flag.getId(), eateryId, details, LocalDateTime.now());
            adminActionLogRepository.save(log);
        } catch (Exception ignored) {}

        Map<String, String> result = new HashMap<>();
        result.put("message", "Flag resolved successfully.");
        return ResponseEntity.ok(result);
    }

    // Get all flags (for admin dashboard)
    @GetMapping("/flags")
    public ResponseEntity<List<ReviewFlag>> getAllFlags(
            @RequestParam(required = false) String status
    ) {
        List<ReviewFlag> flags;
        if (status != null && !status.isEmpty()) {
            flags = reviewFlagRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            flags = reviewFlagRepository.findAll();
        }
        return ResponseEntity.ok(flags);
    }

    // Note: Approvals are recorded by resolving a flag with action=DISMISS in /flags/{flagId}/resolve

    // Hide a review (reason required)
    @PutMapping("/reviews/{reviewId}/hide")
    public ResponseEntity<?> hideReview(
            @PathVariable Long reviewId,
            @RequestParam String reason
    ) {
        reviewService.hideReviewByAdmin(reviewId, reason);
        Map<String, String> result = new HashMap<>();
        result.put("message", "Review hidden.");
        return ResponseEntity.ok(result);
    }

    // Delete a review (reason required)
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> adminDeleteReview(
            @PathVariable Long reviewId,
            @RequestParam String reason
    ) {
        reviewService.deleteReviewByAdmin(reviewId, reason);
        Map<String, String> result = new HashMap<>();
        result.put("message", "Review deleted.");
        return ResponseEntity.ok(result);
    }
}

