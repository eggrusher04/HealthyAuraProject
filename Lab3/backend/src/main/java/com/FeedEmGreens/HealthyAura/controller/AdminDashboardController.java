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

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private ReviewFlagRepository reviewFlagRepository;

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    // List flags, optionally by status (defaults to PENDING)
    @GetMapping("/flags")
    public ResponseEntity<?> flags(@RequestParam(required = false) String status) {
        try {
            String effective = (status == null || status.isBlank()) ? "PENDING" : status;
            List<ReviewFlag> flags = reviewFlagRepository.findByStatusOrderByCreatedAtDesc(effective);
            return ResponseEntity.ok(flags);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Action could not be completed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

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


