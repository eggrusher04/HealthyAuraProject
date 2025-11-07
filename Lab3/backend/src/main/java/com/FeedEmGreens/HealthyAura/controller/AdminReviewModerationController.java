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

/**
 * Controller responsible for administrative review moderation actions.
 *
 * <p>This controller provides functionality for admins to:
 * <ul>
 *     <li>View and manage flagged reviews</li>
 *     <li>Resolve review flags (approve removal or dismiss)</li>
 *     <li>Hide or delete inappropriate reviews</li>
 *     <li>Record and track administrative moderation actions</li>
 * </ul>
 *
 * <p>Access to all endpoints in this controller is restricted to users with the <strong>ADMIN</strong> role.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/admin/review-moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewModerationController {

    /**
     * Repository for managing {@link ReviewFlag} entities.
     */
    @Autowired
    private ReviewFlagRepository reviewFlagRepository;

    /**
     * Service providing review moderation logic such as hiding or deleting reviews.
     */
    @Autowired
    private ReviewService reviewService;

    /**
     * Repository for accessing {@link Users} data, used to associate admin actions with user accounts.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for persisting administrative action logs ({@link AdminActionLog}).
     */
    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    /**
     * Retrieves all pending review flags awaiting moderation.
     *
     * <p>This endpoint returns the list of all flagged reviews currently marked with status <code>PENDING</code>,
     * representing the moderation queue for admins.</p>
     *
     * @return a {@link ResponseEntity} containing the list of pending {@link ReviewFlag} objects
     */

    // Get all pending flagged reviews (moderation queue)
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewFlag>> getPendingFlags() {
        List<ReviewFlag> flags = reviewService.getPendingFlags();
        return ResponseEntity.ok(flags);
    }

    /**
     * Resolves a review flag by either approving the removal of the flagged review or dismissing the flag.
     *
     * <p>When resolved, the flag’s status is updated accordingly:
     * <ul>
     *     <li><code>REMOVE</code> → Status becomes <code>RESOLVED</code></li>
     *     <li><code>DISMISS</code> → Status becomes <code>DISMISSED</code></li>
     * </ul>
     * A record of this moderation action is also logged via {@link AdminActionLogRepository}.</p>
     *
     * @param flagId the ID of the flagged review to resolve
     * @param action the action taken, either <code>REMOVE</code> or <code>DISMISS</code>
     * @param notes  optional moderator notes describing the decision
     * @return a {@link ResponseEntity} containing a success message or an error response
     * @throws IllegalArgumentException if the flag cannot be found or is already resolved
     */

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

    /**
     * Retrieves all flagged reviews, optionally filtered by their moderation status.
     *
     * <p>If no status is specified, all flags are returned. This endpoint is commonly used
     * in the admin dashboard for comprehensive visibility of all flagged reviews.</p>
     *
     * @param status optional filter for flag status (e.g., <code>PENDING</code>, <code>RESOLVED</code>, <code>DISMISSED</code>)
     * @return a {@link ResponseEntity} containing the list of {@link ReviewFlag} objects
     */

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

    /**
     * Hides a review from public visibility, with a specified reason provided by the admin.
     *
     * <p>This action does not permanently delete the review but marks it as hidden
     * for moderation or policy-related reasons.</p>
     *
     * @param reviewId the ID of the review to hide
     * @param reason   the reason for hiding the review
     * @return a {@link ResponseEntity} containing a success message
     */

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

    /**
     * Permanently deletes a review from the system, with a reason provided by the admin.
     *
     * <p>This endpoint should be used for serious cases such as offensive, false,
     * or inappropriate content. A corresponding admin action log is recorded
     * via the {@link ReviewService} implementation.</p>
     *
     * @param reviewId the ID of the review to delete
     * @param reason   the reason for deletion
     * @return a {@link ResponseEntity} containing a confirmation message
     */

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

