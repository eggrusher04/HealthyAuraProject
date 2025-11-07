package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.entity.AdminActionLog;
import com.FeedEmGreens.HealthyAura.entity.ReviewFlag;
import com.FeedEmGreens.HealthyAura.repository.AdminActionLogRepository;
import com.FeedEmGreens.HealthyAura.repository.ReviewFlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller that provides REST endpoints for the admin dashboard.
 *
 * <p>This controller is accessible only to users with the <strong>ADMIN</strong> role and provides:
 * <ul>
 *     <li>Viewing and filtering of flagged reviews</li>
 *     <li>Review flag analytics (metrics by status, reason, or keyword)</li>
 *     <li>Retrieval of recent administrative actions</li>
 * </ul>
 *
 * <p>All responses are wrapped in {@link ResponseEntity} with appropriate HTTP statuses
 * to handle both success and failure cases gracefully.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    /**
     * Repository for accessing and managing {@link ReviewFlag} data.
     */
    @Autowired
    private ReviewFlagRepository reviewFlagRepository;

    /**
     * Repository for retrieving {@link AdminActionLog} records of administrative activities.
     */
    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    /**
     * Retrieves a list of flagged reviews, optionally filtered by their status.
     *
     * <p>If no status is provided, it defaults to <code>PENDING</code>. The flags are returned
     * in descending order based on their creation time. Each flag is represented as a simplified
     * map containing essential fields (ID, reason, status, timestamps, and linked review ID).</p>
     *
     * @param status optional status filter (e.g., <code>PENDING</code>, <code>RESOLVED</code>)
     * @return a {@link ResponseEntity} containing a list of flagged review maps or an error message
     */

    // List flags, optionally by status (defaults to PENDING)
    @GetMapping("/flags")
    public ResponseEntity<?> flags(@RequestParam(required = false) String status) {
        try {
            String effective = (status == null || status.isBlank()) ? "PENDING" : status;
            List<ReviewFlag> flags = reviewFlagRepository.findByStatusOrderByCreatedAtDesc(effective);

            List<Map<String, Object>> result = flags.stream().map(flag -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", flag.getId());
                map.put("reason", flag.getReason());
                map.put("status", flag.getStatus());
                map.put("createdAt", flag.getCreatedAt());
                map.put("adminNotes", flag.getAdminNotes());
                map.put("reviewedAt", flag.getReviewedAt());
                map.put("reviewId",
                        flag.getReview() != null ? flag.getReview().getId() : null);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Action could not be completed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /**
     * Retrieves flagged reviews filtered by a specific reason and optional status.
     *
     * <p>This endpoint allows admins to focus on high-risk flags related to
     * <em>health</em>, <em>hygiene</em>, or other serious issues that require
     * faster verification compared to general flags (e.g., spam or offensive content).</p>
     *
     * @param reason the keyword used to match flag reasons (e.g., <code>hygiene</code>, <code>health</code>)
     * @param status optional status filter (defaults to <code>PENDING</code>)
     * @return a {@link ResponseEntity} containing a list of matching {@link ReviewFlag} entities or an error message
     */

    // Hygiene/health reported flags: use generic by-reason endpoint below

    // Generic by-reason filter (e.g., reason=health, reason=hygiene) 
    /*
     * A filtered slice of flagged reviews where the reason is “hygiene/health” 
     * (e.g., “food poisoning”, “unclean kitchen”)
     * These may be higher severity/risk and need faster verification and consistent handling, separate from general “spam/offensive” flags.
     */
    @GetMapping("/flags/by-reason")
    public ResponseEntity<?> flagsByReason(@RequestParam String reason,
                                           @RequestParam(required = false) String status) {
        try {
            String effective = (status == null || status.isBlank()) ? "PENDING" : status;
            List<ReviewFlag> flags = reviewFlagRepository
                    .findByStatusAndReasonContainingIgnoreCaseOrderByCreatedAtDesc(effective, reason);
            return ResponseEntity.ok(flags);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Action could not be completed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Provides a summarized snapshot of key metrics related to review flags.
     *
     * <p>The metrics include:
     * <ul>
     *     <li>Total number of pending flags</li>
     *     <li>Grouping of pending flags by reason</li>
     *     <li>Keyword-based classification (e.g., hygiene, health, offensive, spam, other)</li>
     * </ul>
     *
     * <p>This endpoint helps administrators quickly assess the current moderation workload
     * and identify trends in reported reviews.</p>
     *
     * @return a {@link ResponseEntity} containing aggregated metric data or an error message
     */

    // Simple metrics snapshot
    @GetMapping("/metrics")
    public ResponseEntity<?> metrics() {
        try {
            Map<String, Object> data = new HashMap<>();
            List<ReviewFlag> pending = reviewFlagRepository.findByStatusOrderByCreatedAtDesc("PENDING");
            data.put("pendingFlags", pending.size());

            // Count pending flags grouped by reason (case-insensitive)
            Map<String, Long> pendingByReason = pending.stream()
                    .collect(Collectors.groupingBy(
                            rf -> rf.getReason() != null ? rf.getReason().toLowerCase() : "unknown",
                            Collectors.counting()
                    ));
            data.put("pendingByReason", pendingByReason);

            // Keyword buckets (case-insensitive substring match)
            String[] hygieneKw = {"hygiene", "unclean", "dirty", "poison", "sanitation"};
            String[] healthKw = {"health", "allergy", "sick", "ill", "contamination"};
            String[] offensiveKw = {"offensive", "abuse", "harass", "racist"};
            String[] spamKw = {"spam", "scam", "advert"};

            Map<String, Long> pendingByKeywords = new HashMap<>();
            pendingByKeywords.put("hygiene", 0L);
            pendingByKeywords.put("health", 0L);
            pendingByKeywords.put("offensive", 0L);
            pendingByKeywords.put("spam", 0L);
            pendingByKeywords.put("other", 0L);

            for (ReviewFlag rf : pending) {
                String reason = rf.getReason() != null ? rf.getReason().toLowerCase() : "";
                boolean matched = false;
                for (String kw : hygieneKw) { if (reason.contains(kw)) { pendingByKeywords.put("hygiene", pendingByKeywords.get("hygiene") + 1); matched = true; break; } }
                if (!matched) for (String kw : healthKw) { if (reason.contains(kw)) { pendingByKeywords.put("health", pendingByKeywords.get("health") + 1); matched = true; break; } }
                if (!matched) for (String kw : offensiveKw) { if (reason.contains(kw)) { pendingByKeywords.put("offensive", pendingByKeywords.get("offensive") + 1); matched = true; break; } }
                if (!matched) for (String kw : spamKw) { if (reason.contains(kw)) { pendingByKeywords.put("spam", pendingByKeywords.get("spam") + 1); matched = true; break; } }
                if (!matched) { pendingByKeywords.put("other", pendingByKeywords.get("other") + 1); }
            }
            data.put("pendingByKeywords", pendingByKeywords);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Action could not be completed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves a summary of recent administrative actions performed by the currently logged-in admin.
     *
     * <p>This endpoint returns the latest 10 entries from the {@link AdminActionLogRepository},
     * allowing administrators to review their recent moderation history and ensure transparency
     * in administrative activities.</p>
     *
     * @return a {@link ResponseEntity} containing the 10 most recent {@link AdminActionLog} entries or an error message
     */

    // Recent admin actions - latest 10 for current admin
    @GetMapping("/recent-summary")
    public ResponseEntity<?> summary() {
        try {
            String admin = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            List<AdminActionLog> logs = adminActionLogRepository
                    .findTop10ByAdminUsernameOrderByTimestampDesc(admin);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Action could not be completed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}


